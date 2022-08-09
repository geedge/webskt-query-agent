package com.mesasoft.cn.sketch.entity;

import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.sketch.config.ApplicationConfig;
import com.mesasoft.cn.util.ConfigUtils;
import com.mesasoft.cn.util.MariaDbBase;
import com.mesasoft.cn.util.ValidationUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
public class DomainWhois {
    private static final String dataBase = ApplicationConfig.DATABASE;
    private static final String tableName = ApplicationConfig.DOMAIN_WHOIS_TABLENAME;

    private String fqdn;
    private String source;
    private Boolean query_success;
    private Integer match_pattern;
    private String whois_domain;
    private Timestamp whois_update_date;
    private Timestamp whois_create_date;
    private Timestamp whois_expire_date;
    private String whois_email;
    private String whois_ns;
    private String whois_registrar_name;
    private String whois_registrant_org;
    private String whois_registrant_name;
    private String whois_registrant_street;
    private String whois_registrant_city;
    private String whois_registrant_state;
    private String whois_registrant_postcode;
    private String whois_registrant_country;
    private String whois_registrant_phone;


    // category schema
    public DomainWhois(String fqdn,
                       String source,
                       Integer match_pattern,
                       Boolean query_success,
                       String whoisDomain,
                       Date whoisUpdateDate,
                       Date whoisCreateDate,
                       Date whoisExpireDate,
                       String whoisEmail,
                       String whoisNs,
                       String whoisRegistrarName,
                       String whoisRegistrantOrg,
                       String whoisRegistrantName,
                       String whoisRegistrantStreet,
                       String whoisRegistrantCity,
                       String whoisRegistrantState,
                       String whoisRegistrantPostcode,
                       String whoisRegistrantCountry,
                       String whoisRegistrantPhone
    ) {

        this.fqdn = fqdn;
        this.source = source;

        // 没有设置match_pattern，则二级域名为右匹配，其余为全匹配
        if (match_pattern == null) {
            this.match_pattern = ValidationUtils.getMatchPattern(fqdn);
        } else {
            this.match_pattern = match_pattern;
        }

        this.query_success = query_success;
        this.whois_domain = ConfigUtils.getEffectiveString(whoisDomain);
        this.whois_update_date = whoisUpdateDate == null ? null : new Timestamp(whoisUpdateDate.getTime());
        this.whois_create_date = whoisCreateDate == null ? null : new Timestamp(whoisCreateDate.getTime());
        this.whois_expire_date = whoisExpireDate == null ? null : new Timestamp(whoisExpireDate.getTime());
        this.whois_email = ConfigUtils.getEffectiveString(whoisEmail);
        this.whois_ns = ConfigUtils.getEffectiveString(whoisNs);
        this.whois_registrar_name = ConfigUtils.getEffectiveString(whoisRegistrarName);
        this.whois_registrant_org = ConfigUtils.getEffectiveString(whoisRegistrantOrg);
        this.whois_registrant_name = ConfigUtils.getEffectiveString(whoisRegistrantName);
        this.whois_registrant_street = ConfigUtils.getEffectiveString(whoisRegistrantStreet);
        this.whois_registrant_city = ConfigUtils.getEffectiveString(whoisRegistrantCity);
        this.whois_registrant_state = ConfigUtils.getEffectiveString(whoisRegistrantState);
        this.whois_registrant_postcode = ConfigUtils.getEffectiveString(whoisRegistrantPostcode);
        this.whois_registrant_country = ConfigUtils.getEffectiveString(whoisRegistrantCountry);
        this.whois_registrant_phone = ConfigUtils.getEffectiveString(whoisRegistrantPhone);
    }

    public static void insertRecords(List<DomainWhois> whoisFiles, MariaDbBase mariaDbBase) {
        for (DomainWhois whoisFile : whoisFiles) {
            // 生成sql
            String resSql = "INSERT INTO " + dataBase + "." + tableName + ' ' +
                    " (" + whoisFile.getKeys() + ") values" +
                    '(' + whoisFile.getValues() + ')';
            resSql = resSql.replace("'null'", "null");

            mariaDbBase.writeSqlExecute(resSql);
        }
    }

    public static String insertSql(List<DomainWhois> whoisFiles) {
        DomainWhois whoisFile = whoisFiles.get(0);
        // 生成sql
        String resSql = "INSERT INTO " + dataBase + "." + tableName + ' ' +
                " (" + whoisFile.getKeys() + ") values" +
                '(' + whoisFile.getValues() + ')';
        resSql = resSql.replace("'null'", "null");

        return resSql;
    }

    public static void updateRecords(List<DomainWhois> categoryFiles, MariaDbBase mariaDbBase) {
        for (DomainWhois categoryFile : categoryFiles) {

            String resSql = "UPDATE " + dataBase + "." +
                    tableName + ' ' +
                    "SET " + categoryFile.getKeyValues() +
                    ", update_time = current_time() " +
                    " WHERE fqdn = '" + categoryFile.getFqdn() + '\'';
            resSql = resSql.replace("'null'", "null");

            mariaDbBase.writeSqlExecute(resSql);
        }
    }

    public static List<DomainWhois> getDbRecord(List<String> fqdns, MariaDbBase mariaDbBase, String source) throws SQLException {
        String queryFqdns = fqdns.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
        String sql = "SELECT * FROM " + dataBase + "." +
                tableName + ' ' +
                " WHERE fqdn in (" + queryFqdns + ") ";

        return rs2schema(mariaDbBase.querySqlExecute(sql));
    }

    public String getValues() {
        String resString = "'" + fqdn + '\'' +
                ", '" + source + '\'' +
                ", " + query_success +
                ", " + match_pattern +
                ", '" + whois_domain + '\'' +
                ", '" + whois_update_date + '\'' +
                ", '" + whois_create_date + '\'' +
                ", '" + whois_expire_date + '\'' +
                ", '" + whois_email + '\'' +
                ", '" + whois_ns + '\'' +
                ", '" + whois_registrar_name + '\'' +
                ", '" + whois_registrant_org + '\'' +
                ", '" + whois_registrant_name + '\'' +
                ", '" + whois_registrant_street + '\'' +
                ", '" + whois_registrant_city + '\'' +
                ", '" + whois_registrant_state + '\'' +
                ", '" + whois_registrant_postcode + '\'' +
                ", '" + whois_registrant_country + '\'' +
                ", '" + whois_registrant_phone + '\'';

        return resString.replace("'null'", "null");
    }

    public static List<DomainWhois> rs2schema(ResultSet rs) throws SQLException {
        List<DomainWhois> schemaFiles = new ArrayList<>();
        while (rs.next()) {
            schemaFiles.add(
                    new DomainWhois(
                            rs.getString("fqdn"),
                            rs.getString("source"),
                            rs.getInt("match_pattern"),
                            rs.getBoolean("query_success"),
                            rs.getString("whois_domain"),
                            (Date) rs.getDate("whois_update_date"),
                            (Date) rs.getDate("whois_create_date"),
                            (Date) rs.getDate("whois_expire_date"),
                            rs.getString("whois_email"),
                            rs.getString("whois_ns"),
                            rs.getString("whois_registrar_name"),
                            rs.getString("whois_registrant_org"),
                            rs.getString("whois_registrant_name"),
                            rs.getString("whois_registrant_street"),
                            rs.getString("whois_registrant_city"),
                            rs.getString("whois_registrant_state"),
                            rs.getString("whois_registrant_postcode"),
                            rs.getString("whois_registrant_country"),
                            rs.getString("whois_registrant_phone")
                    ));
        }
        return schemaFiles;
    }

    public static JSONObject schema2json(DomainWhois schema) throws SQLException {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("fqdn", schema.getFqdn());
        jsonObject.put("source", schema.getSource());
        jsonObject.put("match_pattern", schema.getMatch_pattern());
        jsonObject.put("query_success", schema.getQuery_success());
        jsonObject.put("whois_domain", schema.getWhois_domain());
        jsonObject.put("whois_update_date", schema.getWhois_update_date());
        jsonObject.put("whois_create_date", schema.getWhois_create_date());
        jsonObject.put("whois_expire_date", schema.getWhois_expire_date());
        jsonObject.put("whois_email", schema.getWhois_email());
        jsonObject.put("whois_ns", schema.getWhois_ns());
        jsonObject.put("whois_registrar_name", schema.getWhois_registrar_name());
        jsonObject.put("whois_registrant_org", schema.getWhois_registrant_org());
        jsonObject.put("whois_registrant_name", schema.getWhois_registrant_name());
        jsonObject.put("whois_registrant_street", schema.getWhois_registrant_street());
        jsonObject.put("whois_registrant_city", schema.getWhois_registrant_city());
        jsonObject.put("whois_registrant_state", schema.getWhois_registrant_state());
        jsonObject.put("whois_registrant_postcode", schema.getWhois_registrant_postcode());
        jsonObject.put("whois_registrant_country", schema.getWhois_registrant_country());
        jsonObject.put("whois_registrant_phone", schema.getWhois_registrant_phone());

        return jsonObject;
    }

    public String getKeys() {
        String resString;
        resString = "fqdn" +
                ", source" +
                ", query_success" +
                ", match_pattern" +
                ", whois_domain" +
                ", whois_update_date" +
                ", whois_create_date" +
                ", whois_expire_date" +
                ", whois_email" +
                ", whois_ns" +
                ", whois_registrar_name" +
                ", whois_registrant_org" +
                ", whois_registrant_name" +
                ", whois_registrant_street" +
                ", whois_registrant_city" +
                ", whois_registrant_state" +
                ", whois_registrant_postcode" +
                ", whois_registrant_country" +
                ", whois_registrant_phone";
        return resString;
    }

    public String getKeyValues() {
        String resString = "query_success=" + query_success +
                ", source='" + source + '\'' +
                ", match_pattern=" + match_pattern +
                ", whois_domain='" + whois_domain + '\'' +
                ", whois_update_date='" + whois_update_date + '\'' +
                ", whois_create_date='" + whois_create_date + '\'' +
                ", whois_expire_date='" + whois_expire_date + '\'' +
                ", whois_email='" + whois_email + '\'' +
                ", whois_ns='" + whois_ns + '\'' +
                ", whois_registrar_name='" + whois_registrar_name + '\'' +
                ", whois_registrant_org='" + whois_registrant_org + '\'' +
                ", whois_registrant_name='" + whois_registrant_name + '\'' +
                ", whois_registrant_street='" + whois_registrant_street + '\'' +
                ", whois_registrant_city='" + whois_registrant_city + '\'' +
                ", whois_registrant_state='" + whois_registrant_state + '\'' +
                ", whois_registrant_postcode='" + whois_registrant_postcode + '\'' +
                ", whois_registrant_country='" + whois_registrant_country + '\'' +
                ", whois_registrant_phone='" + whois_registrant_phone + '\'';

        return resString.replace("'null'", "null");
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
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

    public String getWhois_domain() {
        return whois_domain;
    }

    public void setWhois_domain(String whois_domain) {
        this.whois_domain = whois_domain;
    }

    public Timestamp getWhois_update_date() {
        return whois_update_date;
    }

    public void setWhois_update_date(Timestamp whois_update_date) {
        this.whois_update_date = whois_update_date;
    }

    public Timestamp getWhois_create_date() {
        return whois_create_date;
    }

    public void setWhois_create_date(Timestamp whois_create_date) {
        this.whois_create_date = whois_create_date;
    }

    public Timestamp getWhois_expire_date() {
        return whois_expire_date;
    }

    public void setWhois_expire_date(Timestamp whois_expire_date) {
        this.whois_expire_date = whois_expire_date;
    }

    public String getWhois_email() {
        return whois_email;
    }

    public void setWhois_email(String whois_email) {
        this.whois_email = whois_email;
    }

    public String getWhois_ns() {
        return whois_ns;
    }

    public void setWhois_ns(String whois_ns) {
        this.whois_ns = whois_ns;
    }

    public String getWhois_registrar_name() {
        return whois_registrar_name;
    }

    public void setWhois_registrar_name(String whois_registrar_name) {
        this.whois_registrar_name = whois_registrar_name;
    }

    public String getWhois_registrant_org() {
        return whois_registrant_org;
    }

    public void setWhois_registrant_org(String whois_registrant_org) {
        this.whois_registrant_org = whois_registrant_org;
    }

    public String getWhois_registrant_name() {
        return whois_registrant_name;
    }

    public void setWhois_registrant_name(String whois_registrant_name) {
        this.whois_registrant_name = whois_registrant_name;
    }

    public String getWhois_registrant_street() {
        return whois_registrant_street;
    }

    public void setWhois_registrant_street(String whois_registrant_street) {
        this.whois_registrant_street = whois_registrant_street;
    }

    public String getWhois_registrant_city() {
        return whois_registrant_city;
    }

    public void setWhois_registrant_city(String whois_registrant_city) {
        this.whois_registrant_city = whois_registrant_city;
    }

    public String getWhois_registrant_state() {
        return whois_registrant_state;
    }

    public void setWhois_registrant_state(String whois_registrant_state) {
        this.whois_registrant_state = whois_registrant_state;
    }

    public String getWhois_registrant_postcode() {
        return whois_registrant_postcode;
    }

    public void setWhois_registrant_postcode(String whois_registrant_postcode) {
        this.whois_registrant_postcode = whois_registrant_postcode;
    }

    public String getWhois_registrant_country() {
        return whois_registrant_country;
    }

    public void setWhois_registrant_country(String whois_registrant_country) {
        this.whois_registrant_country = whois_registrant_country;
    }

    public String getWhois_registrant_phone() {
        return whois_registrant_phone;
    }

    public void setWhois_registrant_phone(String whois_registrant_phone) {
        this.whois_registrant_phone = whois_registrant_phone;
    }
}

