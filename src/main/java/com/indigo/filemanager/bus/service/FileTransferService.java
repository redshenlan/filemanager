package com.indigo.filemanager.bus.service;

import com.indigo.filemanager.bus.service.util.OpenOffice2PdfUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

@Service
public class FileTransferService {

    @Value("${openoffice.home}")
    private String openOffice_HOME;
    @Value("${converted.file.type}")
    private String converted_file_type;
    @Value("${openoffice.startservice}")
    private String openoffice_startservice;
    @Value("${saveas.tempdir}")
    private String saveas_tempdir;
    public OutputStream transferPdf(InputStream inputStream,String filetype,String outFileType){
        OutputStream outputStream=null;
        switch(converted_file_type){
            case "1":
                outputStream= OpenOffice2PdfUtil.transferPdf(inputStream,filetype,outFileType,openOffice_HOME,openoffice_startservice);
                break;
            default:
                //
                System.out.println("default");
                break;
        }
        return outputStream;
    }
}
