package com.mesasoft.cn.common;

import org.junit.Test;

/**
 * @description:
 * @author: zhq
 * @create: 2022-03-18
 **/
public class SketchTest {


    @Test
    public void testSendEmail() throws Exception {
      String searchPath =   "D://test//test";
        searchPath= searchPath.replace("\\\\","");
        searchPath =searchPath.replace("//","");
        System.err.println(searchPath);
    }
}
