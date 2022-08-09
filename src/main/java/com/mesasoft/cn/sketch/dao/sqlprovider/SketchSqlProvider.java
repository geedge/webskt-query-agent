package com.mesasoft.cn.sketch.dao.sqlprovider;

import com.mesasoft.cn.SketchApplication;
import com.mesasoft.cn.modules.constant.ConfigConsts;
import com.zhazhapan.util.Checker;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author pantao
 * @since 2018/1/19
 */
public class SketchSqlProvider {

    public String getDomainCategoryListBy(@Param("condition") String condition, @Param("offset") int offset) {
        String sql = new SQL() {{
            SELECT("*");
            FROM("domain_category_reputation");
            if (Checker.isNotEmpty(condition)) {
                WHERE("fqdn like '%" + condition + "'");
            }
            ORDER_BY(SketchApplication.settings.getStringUseEval(ConfigConsts.USER_ORDER_BY_OF_SETTINGS));
        }}.toString();
        int size = SketchApplication.settings.getIntegerUseEval(ConfigConsts.USER_PAGE_SIZE_OF_SETTINGS);
        return sql + " limit " + (offset * size) + "," + size;
    }
}
