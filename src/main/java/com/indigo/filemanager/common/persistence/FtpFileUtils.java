package com.indigo.filemanager.common.persistence;

import com.indigo.filemanager.common.persistence.threadPool.ObjectPool;
import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.persistence.vo.SaveFileResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;



@Slf4j
public class FtpFileUtils implements FileUtils {
    private final ObjectPool<FTPClient> objectPool;

    public FtpFileUtils(ObjectPool<FTPClient> objectPool)
    {
        this.objectPool = objectPool;
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
        FTPClient ftpClient = null;
        try {
            ftpClient = objectPool.borrowObject();
            log.info("开始上传文件");
            inputStream = fileInfo.getFile();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            createDirecroty(fileInfo.getFilepath(),ftpClient);
            ftpClient.makeDirectory(fileInfo.getFilepath());
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            String fileName = getFileName(fileInfo);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            return SaveFileResult.ok(fileInfo.getFileKey()).message("上传文件成功").build();
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("保存ftp内容异常");
        } finally {
            if(ftpClient != null)
            {
                try {
                    objectPool.returnObject(ftpClient);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    log.error(e.getCause().getMessage());
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getCause().getMessage());
                }
            }
        }
    }

    @Override
    public SaveFileResult getFile(FileInfo fileInfo) {
        String fileName = getFileName(fileInfo);
        OutputStream os;
        FTPClient ftpClient = null;
        try {
            ftpClient = objectPool.borrowObject();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            os = new ByteArrayOutputStream();
            if(!ftpClient.retrieveFile(fileName, os))
            {
                return SaveFileResult.fail(fileInfo.getFileKey()).message("获取文件失败！");
            }
            return SaveFileResult.ok(fileInfo.getFileKey()).downFile(os).message("获取文件成功").build();
        } catch (Exception e) {
            log.error("获取文件失败" + e.getMessage(), e.getCause());
            throw new RuntimeException("获取文件失败" + e.getMessage(), e.getCause());
        } finally {
            if (ftpClient != null) {
                try {
                    objectPool.returnObject(ftpClient);
                } catch (InterruptedException|IOException e) {
                    e.printStackTrace();
                    log.error("获取文件失败" + e.getMessage(), e.getCause());
                }
            }
        }
    }

    @Override
    public SaveFileResult delteFile(FileInfo fileInfo) {
        FTPClient ftpClient = null;
        try {
            String fileName = getFileName(fileInfo);
            ftpClient = objectPool.borrowObject();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(fileInfo.getFilepath());
            ftpClient.dele(fileName);
            return SaveFileResult.ok(fileInfo.getFileKey()).message("删除文件成功").build();
        } catch (Exception e) {
            log.error("删除文件失败" + e.getMessage(), e.getCause());
            throw new RuntimeException("删除文件失败" + e.getMessage(), e.getCause());
        } finally {
            if (ftpClient!=null) {
                try {
                    objectPool.returnObject(ftpClient);
                } catch (IOException e) {
                    log.error("ftp连接返回池失败！" + e.getMessage(), e.getCause());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("ftp连接返回池失败！" + e.getMessage(), e.getCause());
                }
            }
        }
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

    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     *
     * @param remote 远程路径
     * @param ftpClient ftp客户端
     * @throws IOException 抛出异常
     */
    private void createDirecroty(String remote, FTPClient ftpClient) throws IOException {
        String prefix = "/";
        String directory = remote + prefix;
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase(prefix) && !changeWorkingDirectory(directory,ftpClient)) {
            int start;
            int end;
            if (directory.startsWith(prefix)) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf(prefix, start);
            StringBuilder path = new StringBuilder();
            while (end > start) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), StandardCharsets.ISO_8859_1);
                path.append("/");
                path.append(subDirectory);
                if (!existFile(path.toString(),ftpClient)) {
                    if (makeDirectory(subDirectory,ftpClient)) {
                        changeWorkingDirectory(subDirectory,ftpClient);
                    } else {
                        log.info("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory,ftpClient);
                    }
                } else {
                    changeWorkingDirectory(subDirectory,ftpClient);
                }
                start = end + 1;
                end = directory.indexOf(prefix, start);
            }
        }
    }

    /**
     * 改变目录路径
     *
     * @param directory 目录路径
     * @param ftpClient ftp客户端
     * @return 返回切换目录路径是否成功
     */
    private boolean changeWorkingDirectory(String directory,FTPClient ftpClient) {
        boolean flag = false;
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

    /**
     * 判断ftp服务器文件是否存在
     * @param path 文件路径
     * @return 返回是否成功
     * @throws IOException 抛出ftp流异常
     */
    private boolean existFile(String path,FTPClient ftpClient) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 创建目录
     *
     * @param dir 目录路径
     * @param ftpClient ftp客户端
     * @return 返回是否成功
     */
    private boolean makeDirectory(String dir,FTPClient ftpClient) {
        boolean flag = false;
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
