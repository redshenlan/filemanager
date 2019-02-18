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

	public CheckSignatureFailureException(SignatureExceptionEnum signatureExceptionType) {
		switch (signatureExceptionType) {
		case NO_HTTP_REQUEST:
			this.code = "no http request";
			this.msg = "无法获取HTTP请求信息";
			break;
		case NO_SIGNATURE_INFO:
			this.code = "no signature info";
			this.msg = "没有签名信息";
			break;
		case INCOMPLETE_SIGNATURE_INFO:
			this.code = "incomplete signature info";
			this.msg = "签名信息不完整";
			break;
		case INVALID_HTTP_METHOD:
			this.code = "invalid http method";
			this.msg = "无效的HTTP请求（仅允许GET、POST、PUT、DELETE）";
			break;
		case NO_MATCHED_ACCESSKEY:
			this.code = "no match access key";
			this.msg = "没有匹配的AccessKey";
			break;
		case SIGNATURE_CHECK_FAILURE:
			this.code = "signature check failure";
			this.msg = "签名校验失败";
			break;
		default:
			this.code = "check signature error";
			this.msg = "签名校验错误";
			break;
		}
	}

}
