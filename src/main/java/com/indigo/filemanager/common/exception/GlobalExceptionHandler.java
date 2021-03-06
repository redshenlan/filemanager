package com.indigo.filemanager.common.exception;

import com.indigo.filemanager.common.ServerResponse;
import com.indigo.filemanager.common.security.sign.exception.CheckSignatureFailureException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * 全局异常处理
 * 业务异常需要继承基础异常类 {@link com.indigo.filemanager.common.exception.BizException}
 * 统一controller的异常处理
 * 对于无法捕获的异常进行统一的返回处理
 * code -- server exception
 * msg -- 异常信息
 * @author lihonglin
 * @time 2019年2月19日15:02:58
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ServerResponse handlerException(Exception e){
        if(e instanceof BizException) {
            return ServerResponse.failure(((BizException) e).getCode(), ((BizException) e).getMsg());
        } else {
            return ServerResponse.failure("server exception",e.getMessage());
        }
    }
    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CheckSignatureFailureException.class)
    public ServerResponse handleCheckSignatureFailureException(CheckSignatureFailureException e) {
        return ServerResponse.failure(e.getCode(), e.getMsg());
    }
}
