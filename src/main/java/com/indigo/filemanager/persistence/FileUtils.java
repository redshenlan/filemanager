package com.indigo.filemanager.persistence;

import com.indigo.filemanager.persistence.vo.FileInfo;
import com.indigo.filemanager.persistence.vo.SaveFileResult;

import java.util.List;

public interface FileUtils {
   List<SaveFileResult> saveBatchFile(FileInfo[] fileInfos);
   SaveFileResult saveFile(FileInfo fileInfo);
   public SaveFileResult getFile(FileInfo fileInfo);
   public SaveFileResult delteFile(FileInfo fileInfo);
   public SaveFileResult updateFile(FileInfo fileInfo);
}
