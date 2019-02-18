package com.indigo.filemanager.common.security.sign.exception;

/**
 * 
 * @author zhangqin
 *
 */
public enum SignatureExceptionEnum {
	
	// 无法获取HTTP请求信息
	NO_HTTP_REQUEST, 
	// 无法获取签名信息
	NO_SIGNATURE_INFO, 
	// 签名信息不完整
	INCOMPLETE_SIGNATURE_INFO,
	// HTTP方法不支持
	INVALID_HTTP_METHOD, 
	// 没有匹配的AccessKey
	NO_MATCHED_ACCESSKEY, 
	// 签名校验失败
	SIGNATURE_CHECK_FAILURE;

}
