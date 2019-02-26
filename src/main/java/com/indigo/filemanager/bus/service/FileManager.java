package com.indigo.filemanager.bus.service;

import java.io.InputStream;

import com.indigo.filemanager.bus.domain.entity.User;
import com.indigo.filemanager.bus.exception.FileOperateFailureException;
import com.indigo.filemanager.common.persistence.vo.FileInfo;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午11:01:50
 */
public interface FileManager {
	
	/**
	 * 文件转换
	 * @param fileInfo
	 * @throws FileOperateFailureException 
	 */
	public void transferFile(FileInfo fileInfo) throws FileOperateFailureException;

	/**
	 * 文件上传
	 * @param fileInfo
	 */
	public void uploadFile(FileInfo fileInfo,User user) throws FileOperateFailureException;
	
	/**
	 * 文件下载
	 * @param filekey
	 * @param user
	 * @return FileOperateFailureException
	 */
	public FileInfo downloadFile(String filekey,User user) throws FileOperateFailureException;
	
	/**
	 * 文件删除
	 * @param filekey
	 * @param user
	 * @throws FileOperateFailureException
	 */
	public void deleteFile(String filekey,User user) throws FileOperateFailureException;
	
	/**
	 * 更新文件(仅更新文件名称)
	 * @param filekey
	 * @param filename
	 * @param user
	 */
	public void updateFile(String filekey,String filename,User user) throws FileOperateFailureException;
	
	/**
	 * 根据filekey获取pdf文件的输入流
	 * @param filekey
	 * @param user
	 * @return
	 * @throws FileOperateFailureException
	 */
	public InputStream getFileFdf(String filekey,User user) throws FileOperateFailureException;
}
