package com.indigo.filemanager.common.persistence.threadPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
@Slf4j
public class FtpClientFactory implements PoolableObjectFactory<FTPClient> {

    @Value("${ftp.hostname}")
    private String hostname = "192.168.1.249";
    @Value("${ftp.port}")
    private Integer port = 21;
    @Value("${ftp.username}")
    private String username = "root";
    @Value("${ftp.password}")
    private String password = "123";

    @Override
    public FTPClient makeObject() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(0);
        try {
            ftpClient.connect(hostname, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.warn("FTPServer refused connection");
                return null;
            }
            boolean result = ftpClient.login(username, password);
            if (!result) {
                throw new RuntimeException("ftpClient登陆失败! userName:" + username+ " ; password:" + password);
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("ftpClient登陆失败! userName:" + username+ " ; password:" + password);
        }
        return ftpClient;
    }

    @Override
    public void destroyObject(FTPClient client) {
        try {
            if (client != null && client.isConnected()) {
                client.logout();
            }
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            try {
                if(client != null)
                {
                    client.disconnect();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    @Override
    public boolean validateObject(FTPClient client) {
        try {
            return client.sendNoOp();
        } catch (IOException e) {
            log.error(e.getMessage(),e.getCause());
        }
        return false;
    }
}
