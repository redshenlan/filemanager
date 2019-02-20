package com.indigo.filemanager.service;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileTransferService {

    @Value("${openoffice_home}")
    private String OpenOffice_HOME;
    @Value("${openoffice_convert_tempdir}")
    private String openoffice_convert_tempdir;
    @Value("${openoffice_startservice}")
    private String openoffice_startservice;
    public OutputStream transferPdf(InputStream inputStream,String filetype,String outFileType){
        if (OpenOffice_HOME.charAt(OpenOffice_HOME.length() - 1) != '/') {
            OpenOffice_HOME += "/";
        }
        Process pro = null;

        try {

//            // 如果目标路径不存在, 则新建该路径
            File outputFile = new File(openoffice_convert_tempdir);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
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
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(openoffice_convert_tempdir+"/"+System.currentTimeMillis()+"."+outFileType));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            converter.convert(inputStream, inputDocumentFormat,bos,outputDocumentFormat);
            connection.disconnect();
            // 封闭OpenOffice服务的进程
            pro.destroy();
            return bos;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pro.destroy();
        }

        return null;

    }


}

