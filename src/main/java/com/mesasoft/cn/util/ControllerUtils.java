package com.mesasoft.cn.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.UnicodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.modules.constant.DefaultValues;
import com.zhazhapan.util.Checker;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author pantao
 * @since 2018/1/30
 */
public class ControllerUtils {

    private ControllerUtils() {
    }

    /**
     * 获取一个简单的响应状态
     *
     * @param isSuccess 是否操作成功
     * @return 响应JSON字符串
     */
    public static String getResponse(boolean isSuccess) {
        JSONObject jsonObject = new JSONObject();
        if (isSuccess) {
            jsonObject.put("status", "success");
        } else {
            jsonObject.put("status", "error");
        }
        return jsonObject.toString();
    }

    /**
     * 加载本地资源
     *
     * @param response 返回的Response
     * @param path     资源路径
     * @param download 直接下载
     */
    public static void loadResource2(HttpServletResponse response, String path, boolean download) throws IOException {
        if (Checker.isNotEmpty(path)) {
            File file = new File(path);
            if (download) {
                response.setContentType(getContentType(file)+";charset=UTF-8");
                setResponseFileName2(response, file.getName());
            }
            FileInputStream in = new FileInputStream(file);
            ServletOutputStream os = response.getOutputStream();
            byte[] b;
            while (in.available() > 0) {
                b = in.available() > 1024 ? new byte[1024] : new byte[in.available()];
                in.read(b, 0, b.length);
                os.write(b, 0, b.length);
            }
            in.close();
            os.flush();
            os.close();
        } else {
            response.sendRedirect(DefaultValues.NOT_FOUND_PAGE);
        }
    }
    public static void loadResource(HttpServletResponse response, String path, boolean download) throws IOException {
        if (Checker.isNotEmpty(path)) {
            File file = new File(path);
            if (download) {
                response.setContentType(getContentType(file));
                setResponseFileName( response, file.getName());
                response.setCharacterEncoding("UTF-8");
            }
            FileInputStream in = new FileInputStream(file);
            ServletOutputStream os = response.getOutputStream();
            byte[] b;
            while (in.available() > 0) {
                b = in.available() > 1024 ? new byte[1024] : new byte[in.available()];
                in.read(b, 0, b.length);
                os.write(b, 0, b.length);
            }
            in.close();
            os.flush();
            os.close();
        } else {
            response.sendRedirect(DefaultValues.NOT_FOUND_PAGE);
        }
    }
    public static String getContentType(File file) {
        String defContentType = "application/octet-stream";
        String fileName = file.getName();
        String fileTyle=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        if (StringUtils.isNotBlank(fileTyle)) {
            String type2 = Contants.CONTENT_TYPES.get(fileTyle);
            if (StringUtils.isNotBlank(type2)) {
                return type2;
            }
        } else {
            String type1 = new MimetypesFileTypeMap().getContentType(file);
            if (StringUtils.isNotBlank(type1)) {
                return type1;
            }
        }
        return defContentType;
    }

    /**
     * 设置响应头的文件名
     *
     * @param response {@link HttpServletResponse}
     * @param fileName 文件名
     */
    public static void setResponseFileName2(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition", "attachment;filename=" + UnicodeUtil.toUnicode(fileName));
    }
    public static void setResponseFileName( HttpServletResponse response, String fileName) throws
            UnsupportedEncodingException {
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"),
                "ISO-8859-1"));
    }

    public static void loadFile(HttpServletRequest request, HttpServletResponse response,String filePath,boolean download) throws  IOException {
        request.setCharacterEncoding("utf-8");
        // 文件存储路径
        // 从请求中获取文件名
        File file=new File(filePath);
        String fileName=file.getName();
        // 创建输出流对象
        ServletOutputStream outputStream = response.getOutputStream();
        //以字节数组的形式读取文件
        byte[] bytes = FileUtil.readBytes(filePath);
        // 设置返回内容格式
        response.setContentType("application/octet-stream;charset=UTF-8");
        // 把文件名按UTF-8取出并按ISO8859-1编码，保证弹出窗口中的文件名中文不乱码
        // 中文不要太多，最多支持17个中文，因为header有150个字节限制。
        response.setHeader("filename", UnicodeUtil.toUnicode(fileName));
        // 这一步一定要在读取文件之后进行，否则文件名会乱码，找不到文件
        fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
        // 设置下载弹窗的文件名和格式（文件名要包括名字和文件格式）
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        // 返回数据到输出流对象中
        outputStream.write(bytes);
        // 关闭流对象
        IoUtil.close(outputStream);

    }
}
