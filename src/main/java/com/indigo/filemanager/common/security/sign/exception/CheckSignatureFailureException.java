package com.indigo.filemanager.common.security.sign.exception;

import com.indigo.filemanager.common.exception.BizException;

/**
 * 
 * @author zhangqin
 *
 */
public class CheckSignatureFailureException extends BizException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1207808857577072693L;

	public CheckSignatureFailureException(SignatureExceptionEnum signatureExceptionType) {
		super(signatureExceptionType.getCode(), signatureExceptionType.getMsg());
	}

}
