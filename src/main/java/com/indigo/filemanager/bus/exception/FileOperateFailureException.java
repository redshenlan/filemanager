package com.indigo.filemanager.bus.exception;

import com.indigo.filemanager.common.exception.BizException;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月20日 上午9:29:19
 */
public class FileOperateFailureException extends BizException {

    /**
     *
     */
    private static final long serialVersionUID = -7686387422296173386L;

    public FileOperateFailureException(FileOperateFailureExceptionEnum exceptionType) {
    	super(exceptionType.getCode(), exceptionType.getMsg());
    }

    public FileOperateFailureException(FileOperateFailureExceptionEnum exceptionType, String msg) {
    	super(exceptionType.getCode(), msg);
    }
}
