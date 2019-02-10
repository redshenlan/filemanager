package com.indigo.filemanager.common.security.sign.exception;

import lombok.Data;

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
		}
	}

}
