package com.indigo.filemanager.common.security.sign.exception;

import lombok.Data;

/**
 * 
 * @author zhangqin
 *
 */
@Data
public class CheckSignatureFailureException extends RuntimeException {
	
	private String code;
	
	private String msg;
	
	public CheckSignatureFailureException(SignatureExceptionEnum signatureExceptionEnum) {
		if(SignatureExceptionEnum.NoSignatureInfo.equals(signatureExceptionEnum)) {
			this.code = "no signature info";
			this.msg = "没有签名信息";
		} else if(SignatureExceptionEnum.SignatureCheckFailure.equals(signatureExceptionEnum)) {
			this.code = "signature check failure";
			this.msg = "签名校验失败";
		} else if(SignatureExceptionEnum.NoMatchAccessKey.equals(signatureExceptionEnum)) {
			this.code = "no match access key";
			this.msg = "没有匹配的AccessKey";
		} else if(SignatureExceptionEnum.NoHttpRequest.equals(signatureExceptionEnum)) {
			this.code = "no http request";
			this.msg = "无法获取HTTP请求信息";
		} else if(SignatureExceptionEnum.InvalidHttpMethod.equals(signatureExceptionEnum)) {
			this.code = "invalid http method";
			this.msg = "无效的HTTP请求（仅允许GET、POST、PUT、DELETE）";
		} else {
			this.code = "check signature error";
			this.msg = "签名校验错误";
		}
	}

}
