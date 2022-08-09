/*
package com.zhazhapan.efo.sketch.service;

import cn.ac.iie.api.ChinaZ;
import cn.ac.iie.dao.MariaDbBase;
import cn.ac.iie.util.MariaDBUtils;
import cn.ac.iie.util.ValidationUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zhazhapan.efo.sketch.api.BrightCloud;
import com.zhazhapan.efo.sketch.api.ChinaZ;
import com.zhazhapan.efo.sketch.config.ApplicationConfig;
import com.zhazhapan.efo.sketch.entity.DomainCategory;
import com.zhazhapan.efo.sketch.entity.DomainWhois;
import com.zhazhapan.efo.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

*/
/**
 * Created with IntelliJ IDEA.
 * User: joy
 * Date: 2021/12/31
 * Time: 11:28 AM
 * Description: No Description
 *//*

public class SketchService {
    private static final Logger LOG = Logger.getLogger(SketchService.class);
    private static final int MAX_DB_BATCH_SIZE = ApplicationConfig.DB_QUERY_BATCH_SIZE;

    private List<String> queryObjects;
    private String curQueryType;

    private MariaDbBase mariaDB;

    long queryNum;
    long dbResultNum;
    long apiResultNum;
    long failedQueryNum;

    public JSONArray getQueryResults(String objectType, String queryType, List<String> queryObjects, String username, Boolean isLocal) throws SQLException, IOException {
        queryNum = 0;
        dbResultNum = 0;
        apiResultNum = 0;
        failedQueryNum = 0;

        this.queryObjects = queryObjects;
        this.curQueryType = queryType;
        JSONArray json = null;

        Connection mariaConn = MariaDBUtils.getConnection();
        Statement mariaStat = mariaConn.createStatement();
        mariaDB = new MariaDbBase(mariaConn, mariaStat);

        //去重
        queryObjects = queryObjects.stream().distinct().collect(Collectors.toList());

        // 校验
        queryObjects = ValidationUtils.getChecked(queryObjects, objectType);
        this.queryNum = queryObjects.size();

        // 执行查询
        if (queryType.equals("domain_category")) {
            json = getDomainCategory(queryObjects, username, isLocal);
        } else if (queryType.equals("domain_whois")) {
            json = getDomainWhois(queryObjects, isLocal);
        } else {
            // TODO： get dns server info
            LOG.error("Wrong query type: " + queryType);
        }

        LOG.info("Query Statistic - number of query objects: " + queryNum
                + "\nresults from local db: " + dbResultNum
                + " results from external api: " + apiResultNum);
        MariaDBUtils.close(mariaStat, mariaConn);
        return json;
    }

    public JSONArray getDomainCategory(List<String> domains, String username, boolean isLocal) throws SQLException, IOException {

        JSONArray results = new JSONArray();

        // 查询本地数据库
        ArrayList<String> objectsFromDB = new ArrayList<>();
        List<List<String>> partitions = Lists.partition(queryObjects, MAX_DB_BATCH_SIZE);
        for (List<String> partition : partitions) { // 批量查询
            List<DomainCategory> dbRecords = DomainCategory.getDbRecord(partition, mariaDB, "brightcloud");
            for (DomainCategory record : dbRecords) {
                objectsFromDB.add(record.getFqdn()); // 保存查询记录
                JSONObject jsonObject = DomainCategory.schema2json(record);
                results.add(jsonObject); // 保存查询结果
            }
        }
        dbResultNum = results.size();

        // 调用api
        List<DomainCategory> bcResults = new ArrayList<>();
        List<String> objectsFromApi = new ArrayList<>(queryObjects);
        objectsFromApi.removeAll(objectsFromDB);
        if (!isLocal && objectsFromApi.size() > 0) {
            BrightCloud brightCloud = new BrightCloud();

            List<List<String>> apiPartitions = Lists.partition(objectsFromApi, ApplicationConfig.API_BC_MAXIMUM_QUERYNUM);
            for (List<String> partition : apiPartitions) { // 批量查询
                List<DomainCategory> recordsFromBcApi = brightCloud.responseSparse(brightCloud.getQueryResults(partition));
                for (DomainCategory record : recordsFromBcApi) {
                    if (record.getQuery_success().equals(true)) {  //查询成功的结果
                        record.setSubmit_user(username);
                        bcResults.add(record);
                        if (bcResults.size() > MAX_DB_BATCH_SIZE) { //超过一定量时写入数据库
                            DomainCategory.insertRecords(bcResults, mariaDB);
                            bcResults.clear();
                        }
                        results.add(DomainCategory.schema2json(record));
                    } else {  // 查询失败的结果
                        failedQueryNum += 1;
                    }
                }
            }
        }
        apiResultNum = results.size() - dbResultNum - failedQueryNum;
        DomainCategory.insertRecords(bcResults, mariaDB);

        if (apiResultNum > 0) { // 记录api调用次数
            OutputStream bcQueryLogStream = new FileOutputStream(ApplicationConfig.API_BC_USE_REPORT_FILE, true);
            OutputStreamWriter bcQueryLogWriter = new OutputStreamWriter(bcQueryLogStream, StandardCharsets.UTF_8);
            Date d = new Date(System.currentTimeMillis());
            bcQueryLogWriter.write(d + "," + "List Query," + "-" + "," + curQueryType + "," + apiResultNum + "\n");
            FileUtils.writerClose(bcQueryLogWriter, bcQueryLogStream);
        }

        return results;
    }

    public JSONArray getDomainWhois(List<String> domains, boolean isLocal) throws SQLException, IOException {

        JSONArray results = new JSONArray();

        // 查询本地数据库
        ArrayList<String> objectsFromDB = new ArrayList<>();
        List<List<String>> partitions = Lists.partition(queryObjects, MAX_DB_BATCH_SIZE);
        for (List<String> partition : partitions) { // 批量查询
            List<DomainWhois> dbRecords =
                    DomainWhois.getDbRecord(partition, mariaDB, "chinaz");
            for (DomainWhois record : dbRecords) {
                objectsFromDB.add(record.getFqdn()); // 保存查询记录
                JSONObject jsonObject = DomainWhois.schema2json(record);
                results.add(jsonObject); // 保存查询结果
            }
        }
        dbResultNum = results.size();

        // 调用api
        List<DomainWhois> chinazResults = new ArrayList<>();
        if (!isLocal) {
            ChinaZ chinaz = new ChinaZ();

            List<String> objectsFromApi = new ArrayList<>(queryObjects);
            objectsFromApi.removeAll(objectsFromDB); // 需要调用api查询的部分对象

            List<List<String>> apiPartitions = Lists.partition(objectsFromApi, ApplicationConfig.API_CHINAZ_MAXIMUM_QUERYNUM);
            for (List<String> partition : apiPartitions) { // 批量查询
                List<DomainWhois> recordsFromApi = chinaz.responseSparse(chinaz.getQueryResults(partition));
                for (DomainWhois record : recordsFromApi) {
                    if (record.getQuery_success().equals(true)) {  // 查询成功的结果
                        chinazResults.add(record);
                        if (chinazResults.size() > MAX_DB_BATCH_SIZE) { // 超过一定量时写入数据库
                            DomainWhois.insertRecords(chinazResults, mariaDB);
                            chinazResults.clear();
                        }
                        results.add(DomainWhois.schema2json(record));
                    } else {  // 查询失败的结果
                        failedQueryNum += 1;
                    }
                }
            }
        }

        DomainWhois.insertRecords(chinazResults, mariaDB);
        apiResultNum = results.size() - dbResultNum - failedQueryNum;
        // todo 不直接写入，维护变量，导出一次
        if (apiResultNum > 0) { // 记录api调用次数
            OutputStream bcQueryLogStream = new FileOutputStream(ApplicationConfig.API_CHINAZ_USE_REPORT_FILE, true);
            OutputStreamWriter bcQueryLogWriter = new OutputStreamWriter(bcQueryLogStream, StandardCharsets.UTF_8);
            Date d = new Date(System.currentTimeMillis());
            bcQueryLogWriter.write(d + "," + "List Query," + "-" + "," + curQueryType + "," + apiResultNum + "\n");
            FileUtils.writerClose(bcQueryLogWriter, bcQueryLogStream);
        }


        return results;
    }


    public static void main(String[] args) throws Exception {
//        String objectType = args[0];
//        String queryType = args[1];
//        List<String> queryObjects = Arrays.asList(args[2].split(","));
//        String username = args[4];
//        boolean isLocal = true;
//        if (args.length >= 3) {
//            isLocal = Boolean.parseBoolean(args[3]);
//        }

//        System.out.println(new SketchService().getQueryResults(objectType, queryType, queryObjects, username, isLocal).toString());

        JSONArray queryResults = new SketchService().getQueryResults("domain",
                "domain_category",
                Arrays.asList("baidu.com", "cctv.com"), "", true);
        System.err.println(queryResults.toJSONString());
        String json = "{\"namespace\":\"log.session\",\"type\":\"record\",\"name\":\"session\",\"fields\":[{\"name\":\"common_log_id\",\"type\":\"long\"},{\"name\":\"common_service\",\"type\":\"long\"},{\"name\":\"common_recv_time\",\"type\":\"long\"},{\"name\":\"common_direction\",\"type\":\"long\"},{\"name\":\"common_l4_protocol\",\"type\":\"string\"},{\"name\":\"common_address_type\",\"type\":\"long\"},{\"name\":\"common_schema_type\",\"type\":\"string\"},{\"name\":\"common_policy_id\",\"type\":\"long\"},{\"name\":\"common_user_tags\",\"type\":\"string\"},{\"name\":\"common_action\",\"type\":\"long\"},{\"name\":\"common_sub_action\",\"type\":\"string\"},{\"name\":\"common_user_region\",\"type\":\"string\"},{\"name\":\"common_client_ip\",\"type\":\"string\"},{\"name\":\"common_client_port\",\"type\":\"long\"},{\"name\":\"common_internal_ip\",\"type\":\"string\"},{\"name\":\"common_entrance_id\",\"type\":\"long\"},{\"name\":\"common_device_id\",\"type\":\"string\"},{\"name\":\"common_egress_link_id\",\"type\":\"long\"},{\"name\":\"common_ingress_link_id\",\"type\":\"long\"},{\"name\":\"common_isp\",\"type\":\"string\"},{\"name\":\"common_device_tag\",\"type\":\"string\"},{\"name\":\"common_data_center\",\"type\":\"string\"},{\"name\":\"common_encapsulation\",\"type\":\"long\"},{\"name\":\"common_tunnels\",\"type\":\"string\"},{\"name\":\"common_sled_ip\",\"type\":\"string\"},{\"name\":\"common_device_group\",\"type\":\"string\"},{\"name\":\"common_app_behavior\",\"type\":\"string\"},{\"name\":\"common_client_location\",\"type\":\"string\"},{\"name\":\"common_client_asn\",\"type\":\"string\"},{\"name\":\"common_subscriber_id\",\"type\":\"string\"},{\"name\":\"common_imei\",\"type\":\"string\"},{\"name\":\"common_imsi\",\"type\":\"string\"},{\"name\":\"common_phone_number\",\"type\":\"string\"},{\"name\":\"common_server_ip\",\"type\":\"string\"},{\"name\":\"common_server_port\",\"type\":\"long\"},{\"name\":\"common_external_ip\",\"type\":\"string\"},{\"name\":\"common_server_location\",\"type\":\"string\"},{\"name\":\"common_server_asn\",\"type\":\"string\"},{\"name\":\"common_protocol_label\",\"type\":\"string\"},{\"name\":\"common_service_category\",\"type\":{\"type\":\"array\",\"items\":\"long\"}},{\"name\":\"common_app_label\",\"type\":\"string\"},{\"name\":\"common_app_id\",\"type\":\"string\"},{\"name\":\"common_userdefine_app_name\",\"type\":\"string\"},{\"name\":\"common_app_surrogate_id\",\"type\":\"string\"},{\"name\":\"common_l7_protocol\",\"type\":\"string\"},{\"name\":\"common_sessions\",\"type\":\"long\"},{\"name\":\"common_c2s_pkt_num\",\"type\":\"long\"},{\"name\":\"common_s2c_pkt_num\",\"type\":\"long\"},{\"name\":\"common_c2s_pkt_diff\",\"type\":\"long\"},{\"name\":\"common_s2c_pkt_diff\",\"type\":\"long\"},{\"name\":\"common_c2s_byte_diff\",\"type\":\"long\"},{\"name\":\"common_s2c_byte_diff\",\"type\":\"long\"},{\"name\":\"common_c2s_byte_num\",\"type\":\"long\"},{\"name\":\"common_s2c_byte_num\",\"type\":\"long\"},{\"name\":\"common_start_time\",\"type\":\"long\"},{\"name\":\"common_end_time\",\"type\":\"long\"},{\"name\":\"common_establish_latency_ms\",\"type\":\"long\"},{\"name\":\"common_con_duration_ms\",\"type\":\"long\"},{\"name\":\"common_stream_dir\",\"type\":\"long\"},{\"name\":\"common_address_list\",\"type\":\"string\"},{\"name\":\"common_has_dup_traffic\",\"type\":\"long\"},{\"name\":\"common_stream_error\",\"type\":\"string\"},{\"name\":\"common_stream_trace_id\",\"type\":\"long\"},{\"name\":\"common_link_info_c2s\",\"type\":\"string\"},{\"name\":\"common_link_info_s2c\",\"type\":\"string\"},{\"name\":\"common_packet_capture_file\",\"type\":\"string\"},{\"name\":\"common_c2s_ipfrag_num\",\"type\":\"long\"},{\"name\":\"common_s2c_ipfrag_num\",\"type\":\"long\"},{\"name\":\"common_c2s_tcp_lostlen\",\"type\":\"long\"},{\"name\":\"common_s2c_tcp_lostlen\",\"type\":\"long\"},{\"name\":\"common_c2s_tcp_unorder_num\",\"type\":\"long\"},{\"name\":\"common_s2c_tcp_unorder_num\",\"type\":\"long\"},{\"name\":\"common_c2s_pkt_retrans\",\"type\":\"long\"},{\"name\":\"common_s2c_pkt_retrans\",\"type\":\"long\"},{\"name\":\"common_c2s_byte_retrans\",\"type\":\"long\"},{\"name\":\"common_s2c_byte_retrans\",\"type\":\"long\"},{\"name\":\"common_tcp_client_isn\",\"type\":\"long\"},{\"name\":\"common_tcp_server_isn\",\"type\":\"long\"},{\"name\":\"common_mirrored_pkts\",\"type\":\"long\"},{\"name\":\"common_mirrored_bytes\",\"type\":\"long\"},{\"name\":\"common_first_ttl\",\"type\":\"long\"},{\"name\":\"common_processing_time\",\"type\":\"long\"},{\"name\":\"http_url\",\"type\":\"string\"},{\"name\":\"http_host\",\"type\":\"string\"},{\"name\":\"http_domain\",\"type\":\"string\"},{\"name\":\"http_request_line\",\"type\":\"string\"},{\"name\":\"http_response_line\",\"type\":\"string\"},{\"name\":\"http_request_header\",\"type\":\"string\"},{\"name\":\"http_response_header\",\"type\":\"string\"},{\"name\":\"http_request_content\",\"type\":\"string\"},{\"name\":\"http_response_content\",\"type\":\"string\"},{\"name\":\"http_request_body\",\"type\":\"string\"},{\"name\":\"http_response_body\",\"type\":\"string\"},{\"name\":\"http_request_body_key\",\"type\":\"string\"},{\"name\":\"http_response_body_key\",\"type\":\"string\"},{\"name\":\"http_proxy_flag\",\"type\":\"long\"},{\"name\":\"http_sequence\",\"type\":\"long\"},{\"name\":\"http_snapshot\",\"type\":\"string\"},{\"name\":\"http_cookie\",\"type\":\"string\"},{\"name\":\"http_referer\",\"type\":\"string\"},{\"name\":\"http_user_agent\",\"type\":\"string\"},{\"name\":\"http_request_content_length\",\"type\":\"string\"},{\"name\":\"http_request_content_type\",\"type\":\"string\"},{\"name\":\"http_response_content_length\",\"type\":\"string\"},{\"name\":\"http_response_content_type\",\"type\":\"string\"},{\"name\":\"http_content_length\",\"type\":\"string\"},{\"name\":\"http_content_type\",\"type\":\"string\"},{\"name\":\"http_set_cookie\",\"type\":\"string\"},{\"name\":\"http_version\",\"type\":\"string\"},{\"name\":\"http_response_latency_ms\",\"type\":\"long\"},{\"name\":\"http_session_duration_ms\",\"type\":\"long\"},{\"name\":\"http_action_file_size\",\"type\":\"long\"},{\"name\":\"mail_protocol_type\",\"type\":\"string\"},{\"name\":\"mail_account\",\"type\":\"string\"},{\"name\":\"mail_to_cmd\",\"type\":\"string\"},{\"name\":\"mail_from_cmd\",\"type\":\"string\"},{\"name\":\"mail_from\",\"type\":\"string\"},{\"name\":\"mail_to\",\"type\":\"string\"},{\"name\":\"mail_cc\",\"type\":\"string\"},{\"name\":\"mail_bcc\",\"type\":\"string\"},{\"name\":\"mail_subject\",\"type\":\"string\"},{\"name\":\"mail_subject_charset\",\"type\":\"string\"},{\"name\":\"mail_content\",\"type\":\"string\"},{\"name\":\"mail_content_charset\",\"type\":\"string\"},{\"name\":\"mail_attachment_name\",\"type\":\"string\"},{\"name\":\"mail_attachment_name_charset\",\"type\":\"string\"},{\"name\":\"mail_attachment_content\",\"type\":\"string\"},{\"name\":\"mail_eml_file\",\"type\":\"string\"},{\"name\":\"mail_snapshot\",\"type\":\"string\"},{\"name\":\"dns_message_id\",\"type\":\"long\"},{\"name\":\"dns_qr\",\"type\":\"long\"},{\"name\":\"dns_opcode\",\"type\":\"long\"},{\"name\":\"dns_aa\",\"type\":\"long\"},{\"name\":\"dns_tc\",\"type\":\"long\"},{\"name\":\"dns_rd\",\"type\":\"long\"},{\"name\":\"dns_ra\",\"type\":\"long\"},{\"name\":\"dns_rcode\",\"type\":\"long\"},{\"name\":\"dns_qdcount\",\"type\":\"long\"},{\"name\":\"dns_ancount\",\"type\":\"long\"},{\"name\":\"dns_nscount\",\"type\":\"long\"},{\"name\":\"dns_arcount\",\"type\":\"long\"},{\"name\":\"dns_qname\",\"type\":\"string\"},{\"name\":\"dns_qtype\",\"type\":\"long\"},{\"name\":\"dns_qclass\",\"type\":\"long\"},{\"name\":\"dns_cname\",\"type\":\"string\"},{\"name\":\"dns_sub\",\"type\":\"long\"},{\"name\":\"dns_rr\",\"type\":\"string\"},{\"name\":\"ssl_version\",\"type\":\"string\"},{\"name\":\"ssl_sni\",\"type\":\"string\"},{\"name\":\"ssl_san\",\"type\":\"string\"},{\"name\":\"ssl_cn\",\"type\":\"string\"},{\"name\":\"ssl_pinningst\",\"type\":\"long\"},{\"name\":\"ssl_intercept_state\",\"type\":\"long\"},{\"name\":\"ssl_passthrough_reason\",\"type\":\"string\"},{\"name\":\"ssl_server_side_latency\",\"type\":\"long\"},{\"name\":\"ssl_client_side_latency\",\"type\":\"long\"},{\"name\":\"ssl_server_side_version\",\"type\":\"string\"},{\"name\":\"ssl_client_side_version\",\"type\":\"string\"},{\"name\":\"ssl_cert_verify\",\"type\":\"long\"},{\"name\":\"ssl_error\",\"type\":\"string\"},{\"name\":\"ssl_con_latency_ms\",\"type\":\"long\"},{\"name\":\"ssl_ja3_fingerprint\",\"type\":\"string\"},{\"name\":\"ssl_ja3_hash\",\"type\":\"string\"},{\"name\":\"ssl_cert_issuer\",\"type\":\"string\"},{\"name\":\"ssl_cert_subject\",\"type\":\"string\"},{\"name\":\"quic_version\",\"type\":\"string\"},{\"name\":\"quic_sni\",\"type\":\"string\"},{\"name\":\"quic_user_agent\",\"type\":\"string\"},{\"name\":\"ftp_account\",\"type\":\"string\"},{\"name\":\"ftp_url\",\"type\":\"string\"},{\"name\":\"ftp_content\",\"type\":\"string\"},{\"name\":\"ftp_link_type\",\"type\":\"string\"},{\"name\":\"bgp_type\",\"type\":\"long\"},{\"name\":\"bgp_as_num\",\"type\":\"string\"},{\"name\":\"bgp_route\",\"type\":\"string\"},{\"name\":\"voip_calling_account\",\"type\":\"string\"},{\"name\":\"voip_called_account\",\"type\":\"string\"},{\"name\":\"voip_calling_number\",\"type\":\"string\"},{\"name\":\"voip_called_number\",\"type\":\"string\"},{\"name\":\"sip_call_id\",\"type\":\"string\"},{\"name\":\"sip_originator_description\",\"type\":\"string\"},{\"name\":\"sip_responder_description\",\"type\":\"string\"},{\"name\":\"sip_user_agent\",\"type\":\"string\"},{\"name\":\"sip_server\",\"type\":\"string\"},{\"name\":\"sip_originator_sdp_connect_ip\",\"type\":\"string\"},{\"name\":\"sip_originator_sdp_media_port\",\"type\":\"long\"},{\"name\":\"sip_originator_sdp_media_type\",\"type\":\"string\"},{\"name\":\"sip_originator_sdp_content\",\"type\":\"string\"},{\"name\":\"sip_responder_sdp_connect_ip\",\"type\":\"string\"},{\"name\":\"sip_responder_sdp_media_port\",\"type\":\"long\"},{\"name\":\"sip_responder_sdp_media_type\",\"type\":\"string\"},{\"name\":\"sip_responder_sdp_content\",\"type\":\"string\"},{\"name\":\"sip_duration_s\",\"type\":\"long\"},{\"name\":\"sip_bye\",\"type\":\"string\"},{\"name\":\"rtp_payload_type_c2s\",\"type\":\"long\"},{\"name\":\"rtp_payload_type_s2c\",\"type\":\"long\"},{\"name\":\"rtp_pcap_path\",\"type\":\"string\"},{\"name\":\"rtp_originator_dir\",\"type\":\"long\"},{\"name\":\"ssh_version\",\"type\":\"string\"},{\"name\":\"ssh_auth_success\",\"type\":\"string\"},{\"name\":\"ssh_client_version\",\"type\":\"string\"},{\"name\":\"ssh_server_version\",\"type\":\"string\"},{\"name\":\"ssh_cipher_alg\",\"type\":\"string\"},{\"name\":\"ssh_mac_alg\",\"type\":\"string\"},{\"name\":\"ssh_compression_alg\",\"type\":\"string\"},{\"name\":\"ssh_kex_alg\",\"type\":\"string\"},{\"name\":\"ssh_host_key_alg\",\"type\":\"string\"},{\"name\":\"ssh_host_key\",\"type\":\"string\"},{\"name\":\"ssh_hassh\",\"type\":\"string\"},{\"name\":\"stratum_cryptocurrency\",\"type\":\"string\"},{\"name\":\"stratum_mining_pools\",\"type\":\"string\"},{\"name\":\"stratum_mining_program\",\"type\":\"string\"},{\"name\":\"streaming_media_url\",\"type\":\"string\"},{\"name\":\"streaming_media_protocol\",\"type\":\"string\"},{\"name\":\"app_extra_info\",\"type\":\"string\"}]}";
        Object parse = JSONObject.parse(json);

        System.err.println(parse);
    }
}
*/
