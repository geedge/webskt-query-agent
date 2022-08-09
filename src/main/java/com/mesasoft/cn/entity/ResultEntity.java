package com.mesasoft.cn.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultEntity<T> implements Serializable {

    private int status;
    private String code;
    private String message;
    private T data;
}