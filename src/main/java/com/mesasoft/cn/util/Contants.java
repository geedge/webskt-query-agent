package com.mesasoft.cn.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @description,
 * @author, zhq
 * @create, 2022-03-18
 **/
public class Contants {

   public static final Map<String, String> CONTENT_TYPES = ImmutableMap.<String, String>builder()
            .put("doc", "application/msword")
            .put("bin", "application/octet-stream")
            .put("exe", "application/octet-stream")
            .put("so", "application/octet-stream")
            .put("dll", "application/octet-stream")
            .put("pdf", "application/pdf")
            .put("ai", "application/postscript")
            .put("xls", "application/vnd.ms-excel")
            .put("ppt", "application/vnd.ms-powerpoint")
            .put("dir", "application/x-director")
            .put("js", "application/x-javascript")
            .put("swf", "application/x-shockwave-flash")
            .put("xhtml", "application/xhtml+xml")
            .put("xht", "application/xhtml+xml")
            .put("zip", "application/zip")
            .put("mid", "audio/midi")
            .put("midi", "audio/midi")
            .put("mp3", "audio/mpeg")
            .put("rm", "audio/x-pn-realaudio")
            .put("rpm", "audio/x-pn-realaudio-plugin")
            .put("wav", "audio/x-wav")
            .put("bmp", "image/bmp")
            .put("gif", "image/gif")
            .put("jpeg", "image/jpeg")
            .put("jpg", "image/jpeg")
            .put("png", "image/png")
            .put("css", "text/css")
            .put("html", "text/html")
            .put("htm", "text/html")
            .put("txt", "text/plain")
            .put("xsl", "text/xml")
            .put("xml", "text/xml")
            .put("mpeg", "video/mpeg")
            .put("mpg", "video/mpeg")
            .put("avi", "video/x-msvideo")
            .put("movie", "video/x-sgi-movie")
            .put(".csv", "text/csv"
            ).build();

}
