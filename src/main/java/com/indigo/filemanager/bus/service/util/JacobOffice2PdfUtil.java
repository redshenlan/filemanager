package com.indigo.filemanager.bus.service.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;

/***
 *  * office文件转换为PDF文件  *   * @author leo.li  *  
 */
public class JacobOffice2PdfUtil {

    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;

    private Logger logger = Logger.getLogger(JacobOffice2PdfUtil.class);


    public static int convert2PDF(String inputFile, String pdfFile) {
        String kind = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            System.out.println("文件不存在");
            return -2;
        }
        if (kind.equals("pdf")) {
            System.out.println("原文件就是PDF文件");
            return -3;
        }
        if (kind.equals("doc") || kind.equals("docx") || kind.equals("txt")) {
            return word2PDF(inputFile, pdfFile);
        } else if (kind.equals("ppt") || kind.equals("pptx")) {
            return ppt2PDF(inputFile, pdfFile);
        } else if (kind.equals("xls") || kind.equals("xlsx")) {
            return Ex2PDF(inputFile, pdfFile);
        } else {
            return -4;
        }
    }

    public static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    public static int word2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(true);
            // 打开Word应用程序
            ActiveXComponent app = new ActiveXComponent("KWPS.Application");
            System.out.println("开始转化Word为PDF...");
            long date = new Date().getTime();
            // 设置Word不可见
            app.setProperty("Visible", new Variant(false));
            // 禁用宏
            app.setProperty("AutomationSecurity", new Variant(3));
            // 获得Word中所有打开的文档，返回documents对象
            Dispatch docs = app.getProperty("Documents").toDispatch();
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
//            Dispatch doc = Dispatch.invoke(
//                    docs,
//                    "Open",
//                    Dispatch.Method,
//                    new Object[] { inputFile, new Variant(false),
//                            new Variant(false) }, new int[]{}).toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, false).toDispatch();
            Dispatch.put(doc, "TrackRevisions", new Variant(false));
            Dispatch.put(doc, "PrintRevisions", new Variant(false));
            Dispatch.put(doc, "ShowRevisions", false);
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF);// word保存为pdf格式宏，值为17
            // 关闭文档
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);

            Dispatch.call(doc, "Close", false);
            // 关闭Word应用程序
            app.invoke("Quit", 0);
            ComThread.Release();
            return time;
        } catch (Exception e) {
            return -1;
        }
    }


    public static int Ex2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(true);
            ActiveXComponent ax = new ActiveXComponent("KET.Application");
            System.out.println("开始转化Excel为PDF...");
            long date = new Date().getTime();
            ax.setProperty("Visible", false);
            ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();

            Dispatch excel = Dispatch
                    .invoke(excels, "Open", Dispatch.Method,
                            new Object[]{inputFile, new Variant(false), new Variant(false)}, new int[9])
                    .toDispatch();
            // 转换格式
            Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{new Variant(0), // PDF格式=0
                    pdfFile, new Variant(xlTypePDF) // 0=标准 (生成的PDF图片不会变模糊) 1=最小文件
                    // (生成的PDF图片糊的一塌糊涂)
            }, new int[1]);

            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            Dispatch.call(excel, "Close", new Variant(false));

            if (ax != null) {
                ax.invoke("Quit", new Variant[]{});
                ax = null;
            }
            ComThread.Release();
            return time;
        } catch (Exception e) {
            return -1;
        }
    }


    public static int ppt2PDF(String inputFile, String pdfFile) {
        System.out.println("开始转化PPT为PDF...");
        try {
            ComThread.InitSTA(true);
            ActiveXComponent app = new ActiveXComponent("KWPP.Application");
//            app.setProperty("Visible", false);
            long date = new Date().getTime();
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true, // ReadOnly
                    // false, // Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
            ).toDispatch();
            Dispatch.invoke(ppt, "SaveAs", Dispatch.Method, new Object[]{pdfFile, new Variant(ppSaveAsPDF)},
                    new int[1]);
            System.out.println("PPT");
            Dispatch.call(ppt, "Close");
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            app.invoke("Quit");
            ComThread.Release();
            return time;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    // 删除多余的页，并转换为PDf
    public static void interceptPPT(String inputFile, String pdfFile) {
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA(true);
            app = new ActiveXComponent("KWPP.Application");
            ActiveXComponent presentations = app.getPropertyAsComponent("Presentations");
            ActiveXComponent presentation = presentations.invokeGetComponent("Open", new Variant(inputFile),
                    new Variant(false));
            int count = Dispatch.get(presentations, "Count").getInt();
            System.out.println("打开文档数:" + count);
            ActiveXComponent slides = presentation.getPropertyAsComponent("Slides");
            int slidePages = Dispatch.get(slides, "Count").getInt();
            System.out.println("ppt幻灯片总页数:" + slidePages);

            // 总页数的20%取整+1 最多不超过5页
            int target = (int) (slidePages * 0.5) + 1 > 5 ? 5 : (int) (slidePages * 0.5) + 1;
            // 删除指定页数
            while (slidePages > target) {
                // 选中指定页幻灯片
                Dispatch slide = Dispatch.call(presentation, "Slides", slidePages).toDispatch();
                Dispatch.call(slide, "Select");
                Dispatch.call(slide, "Delete");
                slidePages--;
                System.out.println("当前ppt总页数:" + slidePages);
            }
            Dispatch.invoke(presentation, "SaveAs", Dispatch.Method, new Object[]{pdfFile, new Variant(32)},
                    new int[1]);
            Dispatch.call(presentation, "Save");
            Dispatch.call(presentation, "Close");
            presentation = null;
            app.invoke("Quit");
            app = null;
            ComThread.Release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
