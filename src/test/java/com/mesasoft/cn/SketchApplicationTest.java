package com.mesasoft.cn;

import com.zhazhapan.config.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SketchApplicationTest {

    public static void setSettings() {
        try {
            SketchApplication.settings = new JsonParser(SketchApplicationTest.class.getResource("/config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextLoads() {
    }

}
