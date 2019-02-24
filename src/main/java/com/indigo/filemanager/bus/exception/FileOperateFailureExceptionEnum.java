package com.indigo.filemanager.bus.exception;
/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月20日 上午9:31:35
 */
public enum FileOperateFailureExceptionEnum {

	//文件持久化失败
	FILE_PERSISTENCE_FAIL,
	//转换原文件不存在
	FILE_FILENOEXISTS,
	//不支持转pdf
	FILE_TRANSFER_NOTSUPPORT_PDF,
	//转pdf失败
	FILE_TRANSFER_FAIL,
	//可转换pdf的文件类型配置为空
	FILE_TRANSFER_NOCONFIG,
	//未找到文件数据库记录
	FILE_RECORD_NOT_FOUND,
	//IO出错
	IO_ERROR;
}
