package com.indigo.filemanager.common.security.sign.exception;

import lombok.Data;

@Data
public class CheckSignatureFailureException extends RuntimeException {
	
	private String code;
	
	private String msg;
	
	public CheckSignatureFailureException(String code) {
		
	}

}
