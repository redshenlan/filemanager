package com.indigo.filemanager.common.persistence;

import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.persistence.vo.SaveFileResult;
import lombok.extern.log4j.Log4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


@Log4j
public class FtpFileUtils implements FileUtils {
    //ftp服务器地址
    @Value("${ftp.hostname}")
    private String hostname = "192.168.1.249";
    //ftp服务器端口号默认为21
    @Value("${ftp.port}")
    private Integer port = 21;
    //ftp登录账号
    @Value("${ftp.username}")
    private String username = "root";
    //ftp登录密码
    @Value("${ftp.password}")
    private String password = "123";

    private FTPClient ftpClient = null;

    /**
     * 初始化ftp服务器
     */
    public void initFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            log.info("connecting...ftp服务器:" + this.hostname + ":" + this.port);
            ftpClient.connect(hostname, port); //连接ftp服务器
            ftpClient.login(username, password); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.info("connect failed...ftp服务器:" + this.hostname + ":" + this.port);
            }
            log.info("connect successfu...ftp服务器:" + this.hostname + ":" + this.port);
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<SaveFileResult> saveBatchFile(FileInfo[] fileInfos) {
        List<SaveFileResult> list = new ArrayList<>(16);
        for (FileInfo fileInfo : fileInfos) {
            list.add(saveFile(fileInfo));
        }
        return list;
    }

    @Override
    public SaveFileResult saveFile(FileInfo fileInfo) {
        InputStream inputStream = null;
        try {
            log.info("开始上传文件");
            inputStream = fileInfo.getFile();
            initFtpClient();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            CreateDirecroty(fileInfo.getFilepath());
            ftpClient.makeDirectory(fileInfo.getFilepath());
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            String fileName = getFileName(fileInfo);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            return SaveFileResult.ok(fileInfo.getFileKey()).message("上传文件成功").build();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return SaveFileResult.fail(null).message("上传文件失败").build();
    }

    @Override
    public SaveFileResult getFile(FileInfo fileInfo) {
        String fileName = getFileName(fileInfo);
        OutputStream os = null;
        try {
            initFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (fileName.equalsIgnoreCase(file.getName())) {
                    os = new ByteArrayOutputStream();
                    ftpClient.retrieveFile(file.getName(), os);
                }
            }
            ftpClient.logout();

            return SaveFileResult.ok(fileInfo.getFileKey()).downFile(os).message("获取文件成功").build();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return SaveFileResult.fail(fileInfo.getFileKey()).message("获取文件失败！");
    }

    @Override
    public SaveFileResult delteFile(FileInfo fileInfo) {
        boolean flag = false;
        try {
            String fileName = getFileName(fileInfo);
            initFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            ftpClient.dele(fileName);
            ftpClient.logout();
            flag = true;
            log.info("删除文件成功");
            return SaveFileResult.ok(fileInfo.getFileKey()).message("删除文件成功").build();
        } catch (Exception e) {
            log.error("删除文件失败" + e.getMessage(), e.getCause());
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("ftp连接关闭失败！" + e.getMessage(), e.getCause());
                }
            }
        }
        return SaveFileResult.fail(fileInfo.getFileKey()).message("删除失败！").build();
    }

    @NotNull
    private String getFileName(FileInfo fileInfo) {
        return fileInfo.getFileKey() + "." + fileInfo.getFileSuffix();
    }

    @Override
    public SaveFileResult updateFile(FileInfo fileInfo) {
        if (delteFile(fileInfo).isResult() && saveFile(fileInfo).isResult()) {
            return SaveFileResult.ok(fileInfo.getFileKey()).message("更新文件成功").build();

        }
        return SaveFileResult.fail(fileInfo.getFileKey()).message("更新文件失败！").build();
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    private boolean CreateDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(directory)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existFile(path)) {
                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory);
                    } else {
                        log.info("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory);
                    }
                } else {
                    changeWorkingDirectory(subDirectory);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    //改变目录路径
    private boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                log.info("进入文件夹" + directory + " 成功！");
            } else {
                log.info("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //判断ftp服务器文件是否存在
    private boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    private boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                log.info("创建文件夹" + dir + " 成功！");

            } else {
                log.info("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
