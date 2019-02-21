package com.indigo.filemanager.bus.exception;

import lombok.Data;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月20日 上午9:29:19
 */
@Data
public class FileOperateFailureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7686387422296173386L;

	private String code;

	private String msg;

	public FileOperateFailureException(FileOperateFailureExceptionEnum exceptionType) {
		switch (exceptionType) {
		case FILE_PERSISTENCE_FAIL:
			this.code = "401";
			this.msg = "文件持久化失败";
			break;
		default:
			this.code = "499";
			this.msg = "文件操作失败";
			break;
		}
	}
	
	public FileOperateFailureException(FileOperateFailureExceptionEnum exceptionType,String msg){
		switch (exceptionType) {
		case IO_ERROR:
			this.code = "402";
			this.msg = msg;
			break;
		default:
			this.code = "499";
			this.msg = "文件操作失败";
			break;
		}
	}
}
