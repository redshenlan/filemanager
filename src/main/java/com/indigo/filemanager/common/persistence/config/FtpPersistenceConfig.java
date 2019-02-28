package com.indigo.filemanager.common.persistence.config;

import com.indigo.filemanager.common.persistence.FileUtils;
import com.indigo.filemanager.common.persistence.FtpFileUtils;
import com.indigo.filemanager.common.persistence.threadPool.FTPClientPool;
import com.indigo.filemanager.common.persistence.threadPool.FtpClientFactory;
import com.indigo.filemanager.common.persistence.threadPool.ObjectPool;
import com.indigo.filemanager.common.persistence.threadPool.PoolableObjectFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = {"ftp.server"},havingValue = "true")
public class FtpPersistenceConfig {
    @Value("${pool.size}")
    private int size;
    @Bean
    @Qualifier("fileUtils")
    public FileUtils ftpFileUtils(ObjectPool<FTPClient> objectPool){
        return new FtpFileUtils(objectPool);
    }
    @Bean
    @Qualifier("objectPool")
    public ObjectPool<FTPClient> ftpClientObjectPool(PoolableObjectFactory<FTPClient> poolableObjectFactory) throws Exception {
        return new FTPClientPool(size,poolableObjectFactory);
    }
    @Bean
    @Qualifier("poolableObjectFactory")
    public PoolableObjectFactory<FTPClient> poolableObjectFactory(){
        return new FtpClientFactory();
    }
}
