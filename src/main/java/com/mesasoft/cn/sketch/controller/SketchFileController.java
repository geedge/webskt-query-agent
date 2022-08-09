package com.mesasoft.cn.sketch.controller;

import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.annotation.AuthInterceptor;
import com.mesasoft.cn.entity.User;
import com.mesasoft.cn.enums.InterceptorLevel;
import com.mesasoft.cn.service.IFileManagerService;
import com.mesasoft.cn.util.ControllerUtils;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.ArrayUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: zhq
 * @create: 2022-03-10
 **/
@RestController
@RequestMapping("/sketch")
@Api(value = "/sketch", description = "文件相关操作")
@Slf4j
public class SketchFileController {

    private final IFileManagerService fileManagerService;
    private final HttpServletRequest request;
    private JSONObject jsonObject;

    @Value("${sketch.path.admin}")
    private String pathAdmin;
    @Value("${sketch.path.user}")
    private String pathUser;

    @Autowired
    public SketchFileController(HttpServletRequest request, IFileManagerService fileManagerService, JSONObject jsonObject) {
        this.fileManagerService = fileManagerService;
        this.jsonObject = jsonObject;
        this.request = request;
    }

    @AuthInterceptor(InterceptorLevel.USER)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(String searchPath) {

        log.info("search path :", searchPath);

        User user = (User) request.getSession().getAttribute(ValueConsts.USER_STRING);
        JSONObject json = new JSONObject();
        String rootPath = "";
        if ("system".equals(user.getUsername())) {
            rootPath = pathAdmin;
        } else {
            rootPath = pathUser;
        }
        searchPath = searchPath.replace("\\\\", "");
        searchPath = searchPath.replace("//", "");
        if (StringUtils.isNotBlank(searchPath) && !rootPath.contains(searchPath)) {
            json.fluentPut("path", searchPath);
        } else {
            json.fluentPut("path", rootPath);
        }
        //返回结果
        jsonObject.put("path", json.getString("path"));
        jsonObject.put("result", fileManagerService.list(json));
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(String destination, MultipartHttpServletRequest request) {
        Map<String, MultipartFile> fileMap = request.getFileMap();
        MultipartFile[] files = ArrayUtils.mapToArray(fileMap, MultipartFile.class);
        jsonObject.put("result", fileManagerService.upload(destination, files));
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response, String path) throws IOException, ClassNotFoundException {
        ControllerUtils.loadResource2(response, path, ValueConsts.TRUE);
    }
}
