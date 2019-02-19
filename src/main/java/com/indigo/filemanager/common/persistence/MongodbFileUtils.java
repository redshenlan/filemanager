package com.indigo.filemanager.common.persistence;

import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.persistence.vo.SaveFileResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类是持久化接口类 {@link com.indigo.filemanager.common.persistence.FileUtils}
 * mongodb 接口调用方法的实现。输入参数 {@link com.indigo.filemanager.common.persistence.vo.FileInfo}
 * 进行统一参数处理。
 * 主要方法有:
 *  批量保存 saveBatchFile
 *  保存文件 saveFile
 *  获取文件 getFile
 *  更新文件 updateFile
 *  删除文件 deleteFile
 * @author lihonglin
 * @since 1.0
 * @time 2019-02-19
 */
@Slf4j
public class MongodbFileUtils implements FileUtils {
    @Autowired
    private MongoClient client;
    @Value("${mongodb.dbname}")
    private String dbname;

    private MongoDatabase getDb() {
        return client.getDatabase(dbname);
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
        String fileName = getString(fileInfo);
        GridFSUploadStream gridFSUploadStream = GridFSBuckets.create(getDb()).openUploadStream(fileName, getGridFSUploadOptions(fileInfo));
        InputStream inputStream = fileInfo.getFile();
        byte[] buffer = new byte[1024];
        try {
            while (inputStream.read(buffer) > 0) {
                gridFSUploadStream.write(buffer);
            }

        } catch (IOException e) {
            log.error("Mongodb保存文件失败："+e.getMessage(),e.getCause());
            gridFSUploadStream.abort();
        } finally {
            gridFSUploadStream.close();
        }
        return SaveFileResult.ok(fileInfo.getFileKey()).message("上传文件成功").build();
    }

    private String getString(FileInfo fileInfo) {
        return fileInfo.getFileKey() + "." + fileInfo.getFileSuffix();
    }

    private GridFSUploadOptions getGridFSUploadOptions(FileInfo fileInfo) {
        GridFSUploadOptions gridFSUploadOptions = new GridFSUploadOptions();
        Document document = new Document();
        document.put("filepath", fileInfo.getFilepath());
        document.put("filekey", fileInfo.getFileKey());
        document.put("filename", fileInfo.getFileKey() + "." + fileInfo.getFileSuffix());
        gridFSUploadOptions.metadata(document);
        gridFSUploadOptions.chunkSizeBytes(256 * 1024);
        return gridFSUploadOptions;
    }

    private GridFSDownloadOptions getGridFSDownloadOptions(FileInfo fileInfo) {
        GridFSDownloadOptions gridFSDownloadOptions = new GridFSDownloadOptions();
        gridFSDownloadOptions.revision(0);
        return gridFSDownloadOptions;
    }

    @Override
    public SaveFileResult getFile(FileInfo fileInfo) {
        Bson filter = Filters.and(Filters.eq("metadata.filepath", fileInfo.getFilepath()), Filters.eq("metadata.filekey", fileInfo.getFileKey()), Filters.eq("metadata.filename", fileInfo.getFileKey() + "." + fileInfo.getFileSuffix()));
        GridFSFindIterable iterable = GridFSBuckets.create(getDb()).find(filter);
        OutputStream outputStream = new ByteArrayOutputStream();
        if (iterable != null && iterable.first() != null) {
            ObjectId objectId = iterable.first().getObjectId();
            GridFSBuckets.create(getDb()).downloadToStream(objectId, outputStream);
            return SaveFileResult.ok(fileInfo.getFileKey()).downFile(outputStream).message("下载文件成功").build();
        } else {
            return SaveFileResult.ok(fileInfo.getFileKey()).downFile(null).message("下载文件失败").build();
        }

    }

    @Override
    public SaveFileResult delteFile(FileInfo fileInfo) {
        Bson filter = Filters.and(Filters.eq("metadata.filepath", fileInfo.getFilepath()), Filters.eq("metadata.filekey", fileInfo.getFileKey()), Filters.eq("metadata.filename", fileInfo.getFileKey() + "." + fileInfo.getFileSuffix()));
        GridFSFindIterable iterable = GridFSBuckets.create(getDb()).find(filter);
        if (iterable != null && iterable.first() != null) {
            ObjectId objectId = iterable.first().getObjectId();
            GridFSBuckets.create(getDb()).delete(objectId);
            return SaveFileResult.ok(fileInfo.getFileKey()).message("删除文件成功").build();
        } else {
            return SaveFileResult.fail(fileInfo.getFileKey()).message("删除文件失败").build();
        }
    }

    @Override
    public SaveFileResult updateFile(FileInfo fileInfo) {
        if (delteFile(fileInfo).isResult() && saveFile(fileInfo).isResult()) {
            return SaveFileResult.ok(fileInfo.getFileKey()).message("更新文件成功").build();
        }
        return SaveFileResult.fail(fileInfo.getFileKey()).message("更新文件失败！").build();
    }
}
