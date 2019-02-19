package com.indigo.filemanager.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * BizException 业务类基础异常类
 *
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {
    private String code;

    private String msg;
}
