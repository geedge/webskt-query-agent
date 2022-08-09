package com.mesasoft.cn.entity;

import com.mesasoft.cn.enums.StatusEnum;


/**
 * @author zhq
 */
public class Result {

    public static ResultEntity success() {
        return new ResultEntity(StatusEnum.SUCCESS.getStatus(), StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMessage(), null);
    }

    public static <T> ResultEntity success(T data) {
        return new ResultEntity(StatusEnum.SUCCESS.getStatus(), StatusEnum.SUCCESS.getCode(), StatusEnum.SUCCESS.getMessage(), data);
    }

    public static ResultEntity fail() {
        return new ResultEntity(StatusEnum.FAIL.getStatus(), StatusEnum.FAIL.getCode(), StatusEnum.FAIL.getMessage(), null);
    }

    public static ResultEntity fail(String message) {
        return new ResultEntity(StatusEnum.FAIL.getStatus(), StatusEnum.FAIL.getCode(), message, null);
    }

    /**
     * @param code
     * @param message
     * @return ResultEntity
     */
    public static ResultEntity fail(String code, String message) {
        return new ResultEntity(StatusEnum.FAIL.getStatus(), code, message, null);
    }

    /**
     *
     * @param status
     * @param code
     * @param message
     * @return ResultEntity
     */
    public static ResultEntity fail(int status, String code, String message) {
        return new ResultEntity(status, code, message, null);
    }
}
