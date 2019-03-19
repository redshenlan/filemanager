package com.indigo.filemanager.bus.service.util;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.indigo.filemanager.bus.service.OwnDocumentFormatRegistry;

import java.io.*;

public class OpenOffice2PdfUtil {
    public static OutputStream transferPdf(InputStream inputStream, String filetype, String outFileType,String OpenOffice_HOME,String openoffice_startservice){
        if (OpenOffice_HOME.charAt(OpenOffice_HOME.length() - 1) != '/') {
            OpenOffice_HOME += "/";
        }
        Process pro = null;

        try {

//             启动OpenOffice的服务
            String command = OpenOffice_HOME
                    + openoffice_startservice;
            pro = Runtime.getRuntime().exec(command);
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
            connection.connect();

            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            // 2:获取Format
            OwnDocumentFormatRegistry factory = new OwnDocumentFormatRegistry();
            DocumentFormat inputDocumentFormat = factory
                    .getFormatByFileExtension(filetype);
            DocumentFormat outputDocumentFormat = factory
                    .getFormatByFileExtension(outFileType);
            // 3:执行转换
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert(inputStream, inputDocumentFormat,bos,outputDocumentFormat);
            connection.disconnect();
            // 封闭OpenOffice服务的进程
            pro.destroy();
            return bos;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(pro!=null){
                pro.destroy();
            }
        }

        return null;

    }
}
