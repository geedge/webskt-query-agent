package com.mesasoft.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.SketchApplication;
import com.mesasoft.cn.modules.constant.ConfigConsts;
import com.mesasoft.cn.service.IConfigService;
import org.springframework.stereotype.Service;

/**
 * @author pantao
 * @since 2018/1/22
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    @Override
    public String getGlobalConfig() {
        JSONObject jsonObject = (JSONObject) SketchApplication.settings.getObjectUseEval(ConfigConsts
                .GLOBAL_OF_SETTINGS).clone();
        jsonObject.remove(ConfigConsts.UPLOAD_PATH_OF_GLOBAL);
        jsonObject.remove(ConfigConsts.TOKEN_PATH_OF_GLOBAL);
        jsonObject.remove(ConfigConsts.UPLOAD_FORM_OF_SETTING);
        return jsonObject.toString();
    }

    @Override
    public String getUserConfig() {
        JSONObject jsonObject = (JSONObject) SketchApplication.settings.getObjectUseEval(ConfigConsts.USER_OF_SETTINGS)
                .clone();
        jsonObject.remove(ConfigConsts.EMAIL_CONFIG_OF_USER);
        return jsonObject.toString();
    }
}
