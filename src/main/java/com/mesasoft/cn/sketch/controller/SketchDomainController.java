package com.mesasoft.cn.sketch.controller;

import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.annotation.AuthInterceptor;
import com.mesasoft.cn.enums.InterceptorLevel;
import com.mesasoft.cn.service.IFileManagerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zhq
 * @create: 2022-03-10
 **/
@RestController
@RequestMapping("/sketch/domain")
@Api(value = "/sketch/domain", description = "文件相关操作")
@Slf4j
public class SketchDomainController {

    private final IFileManagerService fileManagerService;
    private final HttpServletRequest request;
    private JSONObject jsonObject;

    @Value("${sketch.path.admin}")
    private String pathAdmin;
    @Value("${sketch.path.user}")
    private String pathUser;

    @Autowired
    public SketchDomainController(HttpServletRequest request, IFileManagerService fileManagerService, JSONObject jsonObject) {
        this.fileManagerService = fileManagerService;
        this.jsonObject = jsonObject;
        this.request = request;
    }

    @AuthInterceptor(InterceptorLevel.USER)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(String searchPath) {

        log.info("search path :", searchPath);


        return jsonObject.toJSONString();
    }

}
