package com.indigo.filemanager.persistence.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.OutputStream;

@Data
public class FileInfo {
  /**
   * 输入文件流
   */
  private InputStream file;
  /**
   * 输出文件流
   */
  private OutputStream downFile;
  /**
   * filekey 不能为空
   */
  @NotNull
  private String fileKey;
  private String filepath;
  /**
   * file的后缀
   */
  @NotNull
  private String fileSuffix;
}
