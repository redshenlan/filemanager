package com.indigo.filemanager.persistence;

import com.indigo.filemanager.persistence.vo.FileInfo;
import com.indigo.filemanager.persistence.vo.SaveFileResult;

import java.util.List;

public interface FileUtils {
   List<SaveFileResult> saveBatchFile(FileInfo[] fileInfos);
   SaveFileResult saveFile(FileInfo fileInfo);
   SaveFileResult getFile(FileInfo fileInfo);
   SaveFileResult delteFile(FileInfo fileInfo);
   SaveFileResult updateFile(FileInfo fileInfo);
}
