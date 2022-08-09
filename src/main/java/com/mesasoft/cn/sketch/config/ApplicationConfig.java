package com.mesasoft.cn.sketch.config;


import com.mesasoft.cn.util.ConfigUtils;

public class ApplicationConfig {
    public static final String QUERY_OUTPUT_DIR = ConfigUtils.getStringProperty("query.output.dir");

    public static final String QUERY_TYPES_DOMAIN = ConfigUtils.getStringProperty("query.types.domain");
    public static final String QUERY_TYPES_IP = ConfigUtils.getStringProperty("query.types.ip");

    public static final Integer UPDATE_EXPIRED_DAY = ConfigUtils.getIntProperty("update.expired.day"); // 更新任务中过期时间长度（天数）

    public static final Integer QUERY_READIN_BATCH = ConfigUtils.getIntProperty("query.readin.batch");
    public static final Integer QUERY_LOG_FILE_LINE_INTERVAL = ConfigUtils.getIntProperty("query.log.file.line.interval"); // 文件查询时，打印log的读取行数间隔

    // api参数
    // brightcloud
    public static final String API_BC_OEMID = ConfigUtils.getStringProperty("bc.oemid");
    public static final String API_BC_DEVICEID = ConfigUtils.getStringProperty("bc.deviceid");
    public static final String API_BC_UID = ConfigUtils.getStringProperty("bc.uid");
    public static final String API_BC_URL = ConfigUtils.getStringProperty("bc.url");
    public static final String API_BC_METHOD = ConfigUtils.getStringProperty("bc.method");

    public static final String API_BC_ISA1CAT = ConfigUtils.getStringProperty("bc.isa1cat");
    public static final String API_BC_ISREPU = ConfigUtils.getStringProperty("bc.isReputation");
    public static final String API_BC_ISXML = ConfigUtils.getStringProperty("bc.isxml");


    public static final Integer API_BC_MAXIMUM_QUERYNUM = ConfigUtils.getIntProperty("bc.maximum.query.num"); // brightcloud单次查询条数上线
    public static final String API_BC_QUERYTYPE = ConfigUtils.getStringProperty("bc.queryType");

    public static final String API_BC_USE_REPORT_FILE = ConfigUtils.getStringProperty("bc.usereport.filepath"); // brightcloud使用报告导出文件目录
    public static final String API_BC_CATEINFO_FILE = ConfigUtils.getStringProperty("bc.cateinfo.filepath"); // brightcloud使用报告导出文件目录


    // chinaz
    public static final String API_CHINAZ_URL_SINGLE = ConfigUtils.getStringProperty("chinaz.url.single");
    public static final String API_CHINAZ_URL_BATCH = ConfigUtils.getStringProperty("chinaz.url.batch");
    public static final String API_CHINAZ_KEY = ConfigUtils.getStringProperty("chinaz.key");
    public static final Integer API_CHINAZ_MAXIMUM_QUERYNUM = ConfigUtils.getIntProperty("chinaz.maximum.query.num");
    public static final String API_CHINAZ_USE_REPORT_FILE = ConfigUtils.getStringProperty("chinaz.usereport.filepath");

    // Mariadb
    public static final String DATABASE = ConfigUtils.getStringProperty("database");
    public static final String DOMAIN_CATE_TABLENAME = ConfigUtils.getStringProperty("tablename.domain.category");
    public static final String DOMAIN_WHOIS_TABLENAME = ConfigUtils.getStringProperty("tablename.domain.whois");
    public static final String DNS_SERVER_TABLENAME = ConfigUtils.getStringProperty("tablename.dns.server");
    public static final Integer DB_QUERY_BATCH_SIZE = ConfigUtils.getIntProperty("db.query.batch.size");

    // 其他
    public static final String TLD_FILE = ConfigUtils.getStringProperty("tld.file"); // 顶级域名公开列表文件

}
