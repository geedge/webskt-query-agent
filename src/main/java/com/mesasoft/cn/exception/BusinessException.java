package com.mesasoft.cn.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    /**
     * 异常代码
     */
    @Builder.Default
    private int status = 500;

    @Builder.Default
    private String code = "500";

    /**
     * 异常信息
     */
    private String message;

    public BusinessException(String message) {
        this.message = message;
    }


    public BusinessException(String message, Throwable e) {
        super(message, e);
    }

    public BusinessException(int status, String code, String message, Throwable e) {
        super(message, e);
        this.status = status;
        this.code = code;
    }


}
