package com.indigo.filemanager.common.security.sign.aspect;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.indigo.filemanager.common.ServerResponse;
import com.indigo.filemanager.common.security.sign.exception.CheckSignatureFailureException;

@ControllerAdvice
@ResponseBody
public class CheckSignatureExceptionAspect {
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CheckSignatureFailureException.class)
    public ServerResponse handleHttpMessageNotReadableException(CheckSignatureFailureException e) {
        return ServerResponse.failure(e.getCode(), e.getMsg());
    }

}
