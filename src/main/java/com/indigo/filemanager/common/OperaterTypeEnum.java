package com.indigo.filemanager.common;

import lombok.Getter;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月26日 下午4:31:57
 */
@Getter
public enum OperaterTypeEnum {
	
	CREATE("01","创建"),
	READ("02","读取"),
	UPDATE("03","更新"),
	DELETE("04","删除"),
	DOWNLOAD("05","下载");
	
	private String code;
	private String name;
	
	private OperaterTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
}
