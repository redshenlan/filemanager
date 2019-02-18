package com.indigo.filemanager.common.persistence.config;

import com.indigo.filemanager.common.persistence.FileUtils;
import com.indigo.filemanager.common.persistence.MongodbFileUtils;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@ConditionalOnProperty(name = {"mongodb.server"},havingValue = "true")
public class SpringMongoConfig {
    @Value("${mongodb.connect}")
    private String connectString;
    @Bean
    @Qualifier("fileUtils")
    @Primary
    public FileUtils gridFsTemplate() {
        return new MongodbFileUtils();
    }
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(connectString);
//        ReadConcern readConcern =  new ReadConcern(ReadConcernLevel.LOCAL);
//        WriteConcern writeConcern =  new WriteConcern(2,3000);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
//                .readConcern(readConcern)
//                .writeConcern(writeConcern)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient;
    }

}