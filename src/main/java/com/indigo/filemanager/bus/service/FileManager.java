package com.indigo.filemanager.bus.service;

import com.indigo.filemanager.persistence.vo.FileInfo;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午11:01:50
 */
public interface FileManager {

	/**
	 * 文件上传
	 * @param fileInfo
	 */
	public void uploadFile(FileInfo fileInfo) throws Exception;
	
	/**
	 * 文件下载
	 * @param filekey
	 * @return
	 */
	public FileInfo downloadFile(String filekey) throws Exception;
	
}
