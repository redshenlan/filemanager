package com.indigo.filemanager.common.security.sign.exception;

import lombok.Getter;

/**
 * 
 * @author zhangqin
 *
 */
@Getter
public enum SignatureExceptionEnum {
	
	// 无法获取HTTP请求信息
	NO_HTTP_REQUEST("no http request", "无法获取HTTP请求信息"), 
	// 无法获取签名信息
	NO_SIGNATURE_INFO("no signature info", "没有签名信息"), 
	// 签名信息不完整
	INCOMPLETE_SIGNATURE_INFO("incomplete signature info", "签名信息不完整"),
	// HTTP方法不支持
	INVALID_HTTP_METHOD("invalid http method", "无效的HTTP请求（仅允许GET、POST、PUT、DELETE）"), 
	// 没有匹配的AccessKey
	NO_MATCHED_ACCESSKEY("no match access key", "没有匹配的AccessKey"), 
	// 签名校验失败
	SIGNATURE_CHECK_FAILURE("signature check failure", "签名校验失败");
	
	private String code;
	
	private String msg;
	
	private SignatureExceptionEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
}
