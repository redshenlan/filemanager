package com.indigo.filemanager.bus.service;

import com.indigo.filemanager.bus.exception.FileOperateFailureException;
import com.indigo.filemanager.bus.exception.FileOperateFailureExceptionEnum;
import com.indigo.filemanager.bus.service.util.JacobOffice2PdfUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileTransferService {
    @Value("${saveas.tempdir}")
    private String saveas_tempdir;
    @Value("${transfer.file}")
    private String transfer_file;
    /**
     * wps另存为方式转pdf
     *
     * @param inputStream
     * @param filetype
     * @param outFileType
     * @return
     * @throws IOException
     */
    public OutputStream transferPdf(InputStream inputStream, String filetype, String outFileType) throws IOException {
        if (!canTransfer(filetype)) {
            throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_TRANSFER_NOTSUPPORT_PDF);
        }
        OutputStream outputStream = null;

        //临时目录是否存在
        Path path = Paths.get(saveas_tempdir);
        boolean pathExists = Files.exists(path);
        if (!pathExists) {
            Files.createDirectory(path);
        }
        //写原文件
        String fileName = System.currentTimeMillis() + "";
        String filePath = saveas_tempdir + "/" + fileName;
        writtoLocal(filePath + "." + filetype, inputStream);
        //转换
        int back = 0;
        if (!StringUtils.isEmpty(transfer_file)) {
            String[] types = transfer_file.split("\\|");
            if (!StringUtils.isEmpty(types[0]) && (","+types[0]+",").contains("," + filetype + ",")) {
                back = JacobOffice2PdfUtil.word2PDF(filePath + "." + filetype, filePath + "." + outFileType);
            }
            if (!StringUtils.isEmpty(types[1]) && (","+types[1]+",").contains("," + filetype + ",")) {
                back = JacobOffice2PdfUtil.Ex2PDF(filePath + "." + filetype, filePath + "." + outFileType);
            }
            if (!StringUtils.isEmpty(types[2]) && (","+types[2]+",").contains("," + filetype + ",")) {
                back = JacobOffice2PdfUtil.ppt2PDF(filePath + "." + filetype, filePath + "." + outFileType);
            }
        }else{
            throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_TRANSFER_NOCONFIG);
        }
        if (back > 0) {
            FileInputStream fileInputStream = new FileInputStream(filePath + "." + outFileType);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while ((bis.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
        }else{
            throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_TRANSFER_FAIL);
        }
        return outputStream;
    }

    /**
     * 写本地文件
     *
     * @param writFilePath
     * @param inputStream
     * @throws IOException
     */
    private void writtoLocal(String writFilePath, InputStream inputStream) throws IOException {
        RandomAccessFile inFile = new RandomAccessFile(writFilePath, "rw");
        FileChannel inChannel = inFile.getChannel();
        //将inputstream转成文件
        byte[] buffer = new byte[1024];
        while (-1 != inputStream.read(buffer)) {
            ByteBuffer b = ByteBuffer.wrap(buffer);
            inChannel.write(b);
        }
        inChannel.close();
    }

    /**
     * 是否能进行pdf转换
     *
     * @param filetype
     * @return true 可以转 false不能
     */
    public boolean canTransfer(String filetype) {
        if(StringUtils.isEmpty(filetype)){
            throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_TRANSFER_NOCONFIG);
        }
        if(!StringUtils.isEmpty(filetype)){
          String filetypeTemp=transfer_file.replaceAll("\\|",",");
            return ("," + filetypeTemp.toLowerCase() + ",").contains("," + filetypeTemp + ",");
        }
        return false;
    }
}
