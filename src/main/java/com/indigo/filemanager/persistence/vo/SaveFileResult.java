package com.indigo.filemanager.persistence.vo;

import lombok.Getter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lihonglin
 * @Description 文件保存返回结果
 * @since 2019-02-18
 */
@Getter
public class SaveFileResult {
    private InputStream file;
    private OutputStream downFile;
    /**
     * 保存操作是否成功
     */
    private boolean result;
    /**
     * 错误信息
     */
    private String message;
    private String fileKey;

    public SaveFileResult  build() {
        return this;
    }

   public static SaveFileResult ok(String fileKey) {
        SaveFileResult result = new SaveFileResult();
        result.result = true;
        result.message = "成功";
        result.fileKey = fileKey;
        return result;
    }

    public static SaveFileResult fail(String fileKey) {
        SaveFileResult result = new SaveFileResult();
        result.result = false;
        result.message = "失败";
        result.fileKey = fileKey;
        return result;
    }

    public SaveFileResult message(String message) {
        this.message = message;
        return this;
    }

    public SaveFileResult fileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public SaveFileResult file(InputStream file) {
        this.file = file;
        return this;
    }

    public SaveFileResult downFile(OutputStream downfile) {
        this.downFile = downfile;
        return this;
    }

    public SaveFileResult result(boolean result) {
        this.result = result;
        return this;
    }

    public static SaveFileResult get() {
        SaveFileResult result = new SaveFileResult();
        return result;
    }

}
