package com.indigo.filemanager.common.persistence.config;

import com.indigo.filemanager.common.persistence.FileUtils;
import com.indigo.filemanager.common.persistence.FtpFileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = {"ftp.server"},havingValue = "true")
public class FtpPersistenceConfig {
    @Bean
    @Qualifier("fileUtils")
    public FileUtils ftpFileUtils(){
        return new FtpFileUtils();
    }
}
