package com.indigo.filemanager.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * BizException 业务类基础异常类
 *
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BizException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5582767530267780667L;

	private String code;

    private String msg;
}
