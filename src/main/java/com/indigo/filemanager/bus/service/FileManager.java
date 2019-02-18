package com.indigo.filemanager.bus.service;

import java.io.InputStream;

import com.indigo.filemanager.common.persistence.vo.FileInfo;

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
	public void uploadFile(FileInfo fileInfo,String userCode) throws Exception;
	
	/**
	 * 文件下载
	 * @param filekey
	 * @param userCode
	 * @return
	 */
	public FileInfo downloadFile(String filekey,String userCode) throws Exception;
	
	/**
	 * 文件删除
	 * @param filekey
	 * @param userCode
	 * @throws Exception
	 */
	public void deleteFile(String filekey,String userCode) throws Exception;
	
	/**
	 * 文件更新
	 * @param fileInfo
	 * @param userCode
	 * @throws Exception
	 */
	public void updateFile(FileInfo fileInfo,String userCode) throws Exception;
	
	/**
	 * 根据filekey获取pdf文件的输入流
	 * @param filekey
	 * @return
	 * @throws Exception
	 */
	public InputStream getFileFdf(String filekey) throws Exception;
}
