package com.mesasoft.cn.sketch.api;
/*
 * @Description:
 * @Author: chenxu
 * @Date: 2021-12-27 13:59:29
 * @LastEditTime: 2021-12-29 17:05:45
 * @LastEditors: chenxu
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.sketch.config.ApplicationConfig;
import com.mesasoft.cn.sketch.entity.DomainWhois;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChinaZ {
    private static final Logger LOG = Logger.getLogger(ChinaZ.class);
    private final String apiKey = ApplicationConfig.API_CHINAZ_KEY;

    public List<DomainWhois> getQueryFiles(List<String> objectList) {
        List<JSONObject> queryResults = getQueryResults(objectList);
        return responseSparse(queryResults);
    }

    /**
     * @Description: 站长之家单查询
     * @Param : 域名
     * @Return: 查询结果
     */
    public JSONObject getQueryResult(String domain){
        String  urlString = ApplicationConfig.API_CHINAZ_URL_SINGLE;
        Map<String, String> params = new LinkedHashMap<String,String>();
        params.put("key", apiKey);
        params.put("domain", domain);
        return  whoisInfoResolve(doPost(urlString, params),domain);
    }

    /**
     * @Description: 站长之家多域名查询
     * @Param : 域名
     * @Return: 查询结果
     */
    public List<JSONObject> getQueryResults(List<String> domainList){
        String  urlString = ApplicationConfig.API_CHINAZ_URL_SINGLE;
        List<JSONObject> whoisInfoList = new ArrayList<>();
        for (String s : domainList) {
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("key", apiKey);
            params.put("domain", s);
            JSONObject r = doPost(urlString, params);
            whoisInfoList.add(whoisInfoResolve(r, s));
        }
        return  whoisInfoList;
    }

    public List<DomainWhois> responseSparse(List<JSONObject> records){
        List<DomainWhois> whoisFiles = new ArrayList<>();

        for(JSONObject record: records) {
            Boolean querySucess = record.getBoolean("isSuccess");
            if (!querySucess) {
                LOG.error("Failed query. Query response: " + record);
                break;
            }

            String fqdn = record.getString("domain_name");
            String domainName = record.getString("domain_host");
            Integer matchPattern = fqdn.equals(domainName)? 1 : 2 ;
            String source = "chinaz";

            // json处理
            Date creatDate = null;
            Date expiraDate = null;
            java.util.Date tmpDate = record.getDate("domain_whois_create_time");
            if(tmpDate!=null){
                creatDate = new Date(tmpDate.getTime());
            }
            tmpDate = record.getDate("domain_whois_expiration_time");
            if(tmpDate!=null){
                expiraDate = new Date(tmpDate.getTime());
            }
            whoisFiles.add(new DomainWhois(
                    fqdn,
                    source,
                    matchPattern,
                    record.getBoolean("isSuccess"),
                    record.getString("domain_host"),
                    null,
                    creatDate,
                    expiraDate,
                    record.getString("domain_whois_email"),
                    record.getString("domain_whois_name_servers"),
                    record.getString("domain_whois_registrar"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    record.getString("domain_whois_phone")
            ));
        }
        return whoisFiles;
    }
    /**
     * @Description: 解析并重构JSON串
     * @Param : 查询得到的“单个”JSON串
     * @Return: 返回重构的JSON串
     */
    public JSONObject whoisInfoResolve(JSONObject jsonRes,String queryDomain){
        JSONObject whoisInfo = new JSONObject(true);
        JSONObject res = jsonRes.getJSONObject("Result");
        if(jsonRes.get("StateCode").equals(1)){
            whoisInfo.put("isSuccess", jsonRes.get("StateCode"));
            whoisInfo.put("domain_name",queryDomain);
            whoisInfo.put("domain_host", res.get("Host"));
            whoisInfo.put("domain_whois_create_time", res.get("CreationDate"));
            whoisInfo.put("domain_whois_expiration_time", res.get("ExpirationDate"));
            whoisInfo.put("domain_whois_registrar", res.get("Registrar"));
            whoisInfo.put("whois_registrar_name", res.get("ContactPerson"));
            whoisInfo.put("domain_whois_email", res.get("Email"));
            whoisInfo.put("domain_whois_phone", res.get("Phone"));
            whoisInfo.put("domain_whois_name_servers", res.get("DnsServer"));
            whoisInfo.put("domain_whois_status", res.get("DomainStatus"));
        }else{
            whoisInfo.put("isSuccess", jsonRes.get("StateCode"));
        }
        return  whoisInfo;
    }

    /**
     * @Description: 构造批量查询需要的URL
     * @Param : 待查询域名
     * @Return: 拼接好的URL
     */
    public List<String> queryStringBuilder(List<String> domainList){
        //将域名每50个划分一组
        int CHINAZ_REQUEST_LIMIT = 50 ;
        int domainListSize = domainList.size();
        int toIndex = CHINAZ_REQUEST_LIMIT;
        Map domainListMap = new HashMap();
        int keyToken = 0;
        for(int i = 0;i<domainList.size();i+=CHINAZ_REQUEST_LIMIT){
            if(i+CHINAZ_REQUEST_LIMIT>domainListSize){        //作用为toIndex最后没有50条数据则剩余几条newList中就装几条
                toIndex=domainListSize-i;
            }
            List<String> newList = domainList.subList(i,i+toIndex);
            domainListMap.put("keyName"+keyToken, newList);
            keyToken++;
        }
        //将批量查询的域名，构造成CHINAZ的格式
        List<String> domainListString = new ArrayList<>();
        Iterator iter = domainListMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            String urlString = "";
            urlString = String.valueOf(val);
            urlString = urlString.replace(", ","|");
            urlString =urlString.replace("[","").replace("]","");
            domainListString.add(urlString);
        }
        return domainListString;
    }

    /**
     * @Description: 站长之家批量查询-批量提交查询请求，并获得提取任务ID
     * @Param : 域名集合（不能超过50个）
     * @Return: 查询结果
     */
    public String batchRequest_step1(String domainsString){
        String TaskID = "";
        String urlString = ApplicationConfig.API_CHINAZ_URL_BATCH;

        Map<String, String> params = new LinkedHashMap<String,String>();
        if (!Objects.equals(domainsString, "overflow")){
            params.put("domains",domainsString);
            params.put("key", apiKey);
            JSONObject r =  doPost(urlString, params);
            TaskID = r.get("TaskID").toString();
            return TaskID;
        }else{
            return TaskID;
        }

    }

    /**
     * @Description: 站长之家查询-根据提取任务ID查询数据是否采集完成，如果完成则得到Json格式结果
     * @Param : 任务ID
     * @Return: 查询结果
     */
    public JSONObject batchRequest_step2(String TaskID){
        String urlString = ApplicationConfig.API_CHINAZ_URL_BATCH;

        Map<String, String> params = new LinkedHashMap<String,String>();
        params.put("taskid",TaskID);
        JSONObject requestTotal = null;
        requestTotal = doPost(urlString, params);
        return requestTotal;
    }

    /**
     * @Description: 完成如下内容：1）将domain拼接成50个一组；2）调用step_1的API上传数据；3）调用step_2的API获取数据；4）格式整理，输出数据
     * @Param : 域名列表
     * @Return: whois记录列表
     */
    public List<String> batchRequestController(List<String> domainList){

        List<String> result =  new ArrayList<>();
        if (domainList.size()> 5000){
            System.out.println("Too many urls in a http post request!");
        }
        List<String> domainListString = new ArrayList<>();
        List<String>  TaskID = new ArrayList<>();


        // Queue<String> queue = new LinkedList<String>();
        domainListString = queryStringBuilder(domainList);
        //循环发送请求，收集每个请求的TaskID
        for (String domainParam : domainListString) {
            TaskID.add(batchRequest_step1(domainParam));
        }

        for (String s : TaskID) {
            int flag = 0;
            //查询接口数据，如果API仍在查询中，则等待10秒继续访问
            while (flag == 0) {
                JSONObject data = batchRequest_step2(s);
                if (data.get("StateCode").equals(0)) {
                    long timeToSleep = 10L;
                    TimeUnit time = TimeUnit.SECONDS;
                    try {
                        time.sleep(timeToSleep);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted " + "while Sleeping");
                    }
                } else {
                    flag = 1;

                    JSONObject json_result = data.getJSONObject("Result");

                    JSONArray json_data = json_result.getJSONArray("Data");
                    // //对Data内部的数据进行遍历
                    for (int j = 0; j < json_data.size(); j++) {
                        String queryDomain = (String) json_data.getJSONObject(j).get("Domain");
                        result.add(whoisInfoResolve(json_data.getJSONObject(j), queryDomain).toJSONString());
                    }
                }
            }
        }

        return result;
    }

    /**
     * @Description: POST调用API数据
     * @Param : 请求对URL，POST请求体需要添加的 k-v 数据
     * @Return: API JSON数据
     */
    public  JSONObject doPost(String url, Map params){
        JSONObject jsonRes = null;
        try {
            // 定义HttpClient
            CloseableHttpClient client = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            //设置参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Object o : params.keySet()) {
                String name = (String) o;
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));

                //System.out.println(name +"-"+value);
            }
            request.setEntity(new UrlEncodedFormEntity(nvps));
            //发送请求
            HttpResponse  httpResponse = client.execute(request);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            //对放回数据进行处理
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream , StandardCharsets.UTF_8));
            StringBuilder strber = new StringBuilder();
            StringBuilder sbf = new StringBuilder();
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            // 关闭输入流
            inStream.close();
            jsonRes = JSONObject.parseObject(sbf.toString());
        }catch (Exception e) {
            System.out.println("请求接口异常");
        }
        return jsonRes;
    }


    public static void main(String[] args){
        ChinaZ t = new ChinaZ();

        //单查询测试
//        System.out.println(t.singleRequest("aaa.baidu.com"));

        //批量查询测试
         List<String> domainList = new ArrayList<>();
         domainList.add("www.baidu.com");
//         domainList.add("aaa.qq.com");
//         domainList.add("doc.mesalab.com");

//         System.out.println(t.batchRequestController(domainList));
         System.out.println(t.getQueryResults(domainList));
    }
}
