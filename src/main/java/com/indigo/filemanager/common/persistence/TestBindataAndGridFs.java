package com.indigo.filemanager.common.persistence;

import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.DBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.connection.ByteBufferBsonOutput;
import org.bson.*;
import org.bson.io.BasicOutputBuffer;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.io.*;

public class TestBindataAndGridFs {
    private static String filePath = "e:\\FTZ.png";
    public static MongoDatabase mongoClient(){
        ConnectionString connectionString = new ConnectionString("mongodb://192.168.1.223:27017");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient.getDatabase("local");
    }


    public static void testMongodbBinData() throws IOException {
        MongoCollection<Document> collection = mongoClient().getCollection("sldocument");
        Document document = new Document();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File(filePath)));
        byte[] buffer = new byte[stream.available()];
        stream.read(buffer);
        stream.close();
        for(int i=0;i<1;i++) {
            BasicDBObject basicDBObject = new BasicDBObject();
            BsonBinary bsonBinary = new BsonBinary(BsonBinarySubType.BINARY, buffer);
            basicDBObject.put("filename","filename"+i);
            document.put("cesahi",basicDBObject);
            document.put("file", bsonBinary);
            document.put("filename","filename"+i);
            document.put("_id",i);
            collection.insertOne(document);
        }
    }


    public static void getStream(){
        MongoCollection<Document> collection = mongoClient().getCollection("sldocument");
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("filename","filename0");
        FindIterable<Document> findIterable = collection.find(basicDBObject);
        MongoCursor<Document> cursor = findIterable.iterator();
        while(cursor.hasNext())
        {
            Document tmp = cursor.next();
            Binary filedata = tmp.get("file",Binary.class);
            filedata.getData();
        }
    }


    public static void replace() throws IOException {
        MongoCollection<Document> collection = mongoClient().getCollection("sldocument");
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("filename","filename2");
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File(filePath)));
        byte[] buffer = new byte[stream.available()];
        stream.read(buffer);
        stream.close();
        BsonBinary bsonBinary = new BsonBinary(BsonBinarySubType.BINARY, buffer);

        Document document = new Document();
        document.put("file", bsonBinary);
        document.put("filename","filename2");
        document.put("_id",0);
        Document real = new Document();
        real.put("$set",document);
        Document document1 = collection.findOneAndUpdate(basicDBObject,real);
        if(document1 != null)
        {
            System.out.println(document1);
        }
    }

    public static void delete(){
        MongoCollection<Document> collection = mongoClient().getCollection("sldocument");
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("filename","filename2");
        DeleteResult result = collection.deleteMany(basicDBObject);
        if(result.getDeletedCount()>0)
        {
            System.out.println("删除成功！");
        }
    }

    private static GridFSUploadOptions getGridFSUploadOptions(FileInfo fileInfo) {
        GridFSUploadOptions gridFSUploadOptions = new GridFSUploadOptions();
        Document document = new Document();
        document.put("filepath", fileInfo.getFilepath());
        document.put("filekey", fileInfo.getFileKey());
        document.put("filename", fileInfo.getFileKey() + "." + fileInfo.getFileSuffix());
        gridFSUploadOptions.metadata(document);
        gridFSUploadOptions.chunkSizeBytes(15 * 1024*1024);
        return gridFSUploadOptions;
    }
    public static void testMongodbGridFs() throws IOException {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileSuffix("png");
        fileInfo.setFileName("ceshi");
        fileInfo.setFilepath("/ceshi");
        GridFSBucket buckets = GridFSBuckets.create(mongoClient());
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        BufferedInputStream stream1 = new BufferedInputStream(fileInputStream);
        byte[] buffer1 = new byte[stream1.available()];
        stream1.read(buffer1);
        stream1.close();
        ByteArrayInputStream stream = new ByteArrayInputStream(buffer1);
        for(int i=0;i<1000;i++) {
            GridFSUploadStream gridFSUploadStream = buckets.openUploadStream(fileInfo.getFileName(), getGridFSUploadOptions(fileInfo));
            try {
                stream.mark(1000);
                byte[] buffer = new byte[4096];
                int len;
                while((len = stream.read(buffer))>0)
                {
                    gridFSUploadStream.write(buffer,0,len);
                }
                stream.reset();
            } catch (Exception e) {
                gridFSUploadStream.abort();
            } finally {
                gridFSUploadStream.close();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();
        TestBindataAndGridFs.delete();
        System.out.println("耗时："+(timestart - System.currentTimeMillis()));
    }

}
