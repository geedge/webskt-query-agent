package com.mesasoft.cn.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: zhq
 * @create: 2022-03-21
 **/
@Getter
@AllArgsConstructor
public enum StatusEnum {

    SUCCESS(200, "200", "success"),
    FAIL(400, "400", "fail"),
    NOT_FOUND(404, "404", "not found"),
    SERROR_ERROR(500, "500", "not found");

    private int status;
    private String code;
    private String message;
}
