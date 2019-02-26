package com.indigo.filemanager.bus.exception;

import lombok.Getter;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月20日 上午9:31:35
 */
@Getter
public enum FileOperateFailureExceptionEnum {

	// 文件持久化失败
	FILE_PERSISTENCE_FAIL("401", "文件持久化失败"),
	// IO出错
	IO_ERROR("402", "IO出错"),
	// 转换原文件不存在
	FILE_FILENOEXISTS("403", "文件不存在"),
	// 不支持转pdf
	FILE_TRANSFER_NOTSUPPORT_PDF("404", "文件不支持pdf转换"),
	// 转pdf失败
	FILE_TRANSFER_FAIL("405", "转换文件失败"),
	// 可转换pdf的文件类型配置为空
	FILE_TRANSFER_NOCONFIG("406", "可转换pdf的文件类型配置为空"),
	// 未找到文件数据库记录
	FILE_RECORD_NOT_FOUND("407", "未找到文件数据库记录"),
	// 未找到文件持久化信息
	FILE_PERSISTENCE_NOT_FOUND("408", "未找到文件持久化信息"),
	// 文件不能查看（没有对应PDF文件）
	FILE_CAN_NOT_VIEW("409", "文件无法查看");

	private String code;
	private String msg;

	private FileOperateFailureExceptionEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
