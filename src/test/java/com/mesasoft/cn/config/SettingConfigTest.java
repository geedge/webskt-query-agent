package com.mesasoft.cn.config;

import com.mesasoft.cn.SketchApplication;
import com.mesasoft.cn.SketchApplicationTest;
import com.mesasoft.cn.modules.constant.ConfigConsts;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @author pantao
 * @since 2018/1/26
 */
public class SettingConfigTest {

    @Test
    public void testFileSuffixPattern() {
        SketchApplicationTest.setSettings();
        assert Pattern.compile(SketchApplication.settings.getStringUseEval(ConfigConsts.FILE_SUFFIX_MATCH_OF_SETTING)).matcher("jpg").matches();
    }

    @Test
    public void testGetStoragePath() {
        SketchApplicationTest.setSettings();
        System.out.println(SettingConfig.getStoragePath(ConfigConsts.TOKEN_OF_SETTINGS));
    }
}
