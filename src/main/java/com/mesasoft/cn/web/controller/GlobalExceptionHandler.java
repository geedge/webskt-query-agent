package com.mesasoft.cn.web.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mesasoft.cn.entity.Result;
import com.mesasoft.cn.entity.ResultEntity;
import com.mesasoft.cn.enums.StatusEnum;
import com.mesasoft.cn.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Log log = LogFactory.get();


    @ExceptionHandler(AsyncRequestTimeoutException.class) //捕获特定异常
    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
        log.info("Handle Async Request Timeout Exception");
    }


    @ExceptionHandler(Exception.class)
    public ResultEntity handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(StatusEnum.FAIL.getStatus());
        String message = e.getMessage() + (e.getCause() != null ? e.getCause().getMessage() : "");
        log.error("message:{}, stackTrace:{}", message, getStackTrace(e));
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler({BusinessException.class})
    public ResultEntity handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(e.getStatus());
        String message = (e.getMessage() != null ? e.getMessage() : e.getMessage()) + "  " + (e.getCause() != null ? e.getCause().getMessage() : "");
        log.error("message:{}.stackTrace:{}", message, getStackTrace(e));
        return Result.fail(e.getStatus(), e.getCode(), message);
    }

    private String getStackTrace(Exception e) {
        return ObjectUtil.isNotNull(e.getStackTrace()) ? e.getStackTrace()[0].toString() : "";
    }


}
