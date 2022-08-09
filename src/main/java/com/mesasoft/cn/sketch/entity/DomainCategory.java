package com.mesasoft.cn.sketch.entity;

import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.util.ConfigUtils;
import com.mesasoft.cn.sketch.config.ApplicationConfig;
import com.mesasoft.cn.util.MariaDbBase;
import com.mesasoft.cn.util.ValidationUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: joy
 * Date: 2021/12/29
 * Time: 9:27 AM
 * Description: No Description
 */
public class DomainCategory {
    private static final String dataBase = ApplicationConfig.DATABASE;
    private static final String tableName = ApplicationConfig.DOMAIN_CATE_TABLENAME;

    private String fqdn;
    private String source;
    private Boolean query_success;
    private Integer match_pattern;
    private Integer reputation_score;
    private String reputation_level;
    private Integer category_id;
    private String category_name;
    private String category_group;
    private Integer category_conf;
    private Boolean is_a1_cat;
    private Integer status_code = 0;
    private String submit_user ="''";


    // category schema
    public DomainCategory(String fqdn,
                          String source,
                          Boolean query_success,
                          Integer match_pattern,
                          Integer reputation_score,
                          String reputationLevel,
                          Integer categoryId,
                          String categoryName,
                          String categoryGroup,
                          Integer categoryConf,
                          Boolean isA1Cat, Integer statusCode, String submitUser) {

        this.fqdn = fqdn;
        this.source = source; // 默认应为为brightcloud
        this.query_success = query_success;

        // 没有设置match_pattern，则二级域名为右匹配，其余为全匹配
        if (match_pattern == null) {
            this.match_pattern = ValidationUtils.getMatchPattern(fqdn);
        } else {
            this.match_pattern = match_pattern;
        }

        this.reputation_score = reputation_score;
        this.reputation_level = ConfigUtils.getEffectiveString(reputationLevel);
        this.category_id = categoryId;
        this.category_name = ConfigUtils.getEffectiveString(categoryName);
        this.category_group = ConfigUtils.getEffectiveString(categoryGroup);
        this.category_conf = categoryConf;
        this.is_a1_cat = isA1Cat;
        this.status_code = statusCode;
        this.submit_user = submitUser;

    }
    public DomainCategory(String fqdn,
                          String source,
                          Boolean query_success,
                          Integer match_pattern,
                          Integer reputation_score,
                          String reputationLevel,
                          Integer categoryId,
                          String categoryName,
                          String categoryGroup,
                          Integer categoryConf,
                          Boolean isA1Cat) {

        this.fqdn = fqdn;
        this.source = source; // 默认应为为brightcloud
        this.query_success = query_success;

        // 没有设置match_pattern，则二级域名为右匹配，其余为全匹配
        if (match_pattern == null) {
            this.match_pattern = ValidationUtils.getMatchPattern(fqdn);
        } else {
            this.match_pattern = match_pattern;
        }

        this.reputation_score = reputation_score;
        this.reputation_level = ConfigUtils.getEffectiveString(reputationLevel);
        this.category_id = categoryId;
        this.category_name = ConfigUtils.getEffectiveString(categoryName);
        this.category_group = ConfigUtils.getEffectiveString(categoryGroup);
        this.category_conf = categoryConf;
        this.is_a1_cat = isA1Cat;

    }
    public static void insertRecords(List<DomainCategory> categoryFiles, MariaDbBase mariaDbBase) {
        for (DomainCategory categoryFile : categoryFiles) {
            // 生成sql
            String resSql = "INSERT INTO " + dataBase + "." + tableName + ' ' +
                    " (" + categoryFile.getKeys() + ") values" +
                    '(' + categoryFile.getValues() + ')';
            resSql = resSql.replace("'null'", "null");

            mariaDbBase.writeSqlExecute(resSql);
        }
    }

    public void updateRecords(List<DomainCategory> categoryFiles, MariaDbBase mariaDbBase) {
        for (DomainCategory categoryFile : categoryFiles) {

            String resSql = "UPDATE " + dataBase + "." +
                    tableName + ' ' +
                    "SET " + categoryFile.getKeyValues() +
                    ", update_time = current_time() " +
                    " WHERE fqdn = '" + categoryFile.getFqdn() + '\'';
            resSql = resSql.replace("'null'", "null");

            mariaDbBase.writeSqlExecute(resSql);
        }
    }

    public static List<DomainCategory> getDbRecord(List<String> fqdns, MariaDbBase mariaDbBase, String source) throws SQLException {
        String queryFqdns = fqdns.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
        String sql = "SELECT * FROM " + dataBase + "." +
                tableName + ' ' +
                " WHERE fqdn in (" + queryFqdns + ") and source = '" + source + "'";

        return rs2schema(mariaDbBase.querySqlExecute(sql));
    }

    public static List<DomainCategory> rs2schema(ResultSet rs) throws SQLException {
        List<DomainCategory> schemaFiles = new ArrayList<>();
        while (rs.next()) {
            schemaFiles.add(
                    new DomainCategory(
                            rs.getString("fqdn"),
                            rs.getString("source"),
                            rs.getBoolean("query_success"),
                            rs.getInt("match_pattern"),
                            rs.getInt("reputation_score"),
                            rs.getString("reputation_level"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("category_group"),
                            rs.getInt("category_conf"),
                            rs.getBoolean("is_a1_cat"),
                            rs.getInt("status_code"),
                            rs.getString("submit_user")

                    ));
        }
        return schemaFiles;
    }

    public static JSONObject schema2json(DomainCategory schema) throws SQLException {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("fqdn", schema.getFqdn());
        jsonObject.put("source", schema.getSource());
        jsonObject.put("query_success", schema.getQuery_success());
        jsonObject.put("match_pattern", schema.getMatch_pattern());
        jsonObject.put("reputation_score", schema.getReputation_score());
        jsonObject.put("reputation_level", schema.getReputation_level());
        jsonObject.put("category_id", schema.getCategory_id());
        jsonObject.put("category_group", schema.getCategory_group());
        jsonObject.put("category_name", schema.getCategory_name());
        jsonObject.put("category_conf", schema.getCategory_conf());
        jsonObject.put("is_a1_cat", schema.getIs_a1_cat());
        jsonObject.put("status_code", schema.getStatus_code());
        jsonObject.put("submit_user", schema.getSubmit_user());

        return jsonObject;
    }

    public String getValues() {
        String resString = "'" + fqdn + '\'' +
                ", '" + source + '\'' +
                ", " + query_success +
                ", " + match_pattern +
                ", " + reputation_score +
                ", '" + reputation_level + '\'' +
                ", " + category_id +
                ", '" + category_name + '\'' +
                ", '" + category_group + '\'' +
                ", " + category_conf +
                ", " + status_code +
                ", " + submit_user +
                ", " + is_a1_cat;

        return resString.replace("'null'", "null");
    }

    public String getKeys() {
        String resString;
        resString = "fqdn" +
                ", source" +
                ", query_success" +
                ", match_pattern" +
                ", reputation_score" +
                ", reputation_level" +
                ", category_id" +
                ", category_name" +
                ", category_group" +
                ", category_conf" +
                ", status_code" +
                ", submit_user" +
                ", is_a1_cat";
        return resString;
    }

    public String getKeyValues() {
        String resString = "source='" + source + '\'' +
                ", query_success=" + query_success +
                ", match_pattern=" + match_pattern +
                ", reputation_score=" + reputation_score +
                ", reputation_level='" + reputation_level + '\'' +
                ", category_id=" + category_id +
                ", category_name='" + category_name + '\'' +
                ", category_group='" + category_group + '\'' +
                ", category_conf=" + category_conf +
                ", is_a1_cat=" + is_a1_cat;

        return resString.replace("'null'", "null");
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getQuery_success() {
        return query_success;
    }

    public void setQuery_success(Boolean query_success) {
        this.query_success = query_success;
    }

    public Integer getMatch_pattern() {
        return match_pattern;
    }

    public void setMatch_pattern(Integer match_pattern) {
        this.match_pattern = match_pattern;
    }

    public Integer getReputation_score() {
        return reputation_score;
    }

    public void setReputation_score(Integer reputation_score) {
        this.reputation_score = reputation_score;
    }

    public String getReputation_level() {
        return reputation_level;
    }

    public void setReputation_level(String reputation_level) {
        this.reputation_level = reputation_level;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_group() {
        return category_group;
    }

    public void setCategory_group(String category_group) {
        this.category_group = category_group;
    }

    public Integer getCategory_conf() {
        return category_conf;
    }

    public void setCategory_conf(Integer category_conf) {
        this.category_conf = category_conf;
    }

    public Boolean getIs_a1_cat() {
        return is_a1_cat;
    }

    public void setIs_a1_cat(Boolean is_a1_cat) {
        this.is_a1_cat = is_a1_cat;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getSubmit_user() {
        return submit_user;
    }

    public void setSubmit_user(String submit_user) {
        this.submit_user = submit_user;
    }
}
