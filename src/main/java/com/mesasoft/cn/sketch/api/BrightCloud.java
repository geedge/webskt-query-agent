package com.mesasoft.cn.sketch.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mesasoft.cn.sketch.config.ApplicationConfig;
import com.mesasoft.cn.sketch.entity.DomainCategory;
import com.mesasoft.cn.util.FileUtils;
import com.mesasoft.cn.util.ValidationUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yjy
 * @version 1.0
 * @date 2021/2/22 2:37 下午
 */

public class BrightCloud {
    private static final Logger LOG = Logger.getLogger(BrightCloud.class);

    private final Integer maxObjectNum = ApplicationConfig.API_BC_MAXIMUM_QUERYNUM;
    private final HashMap<Integer, List<String>> catId2Info = new HashMap<>();
    private HttpURLConnection con;

    public List<DomainCategory> getQueryFiles(List<String> domains){
        JSONObject queryResults = getQueryResults(domains);
        return responseSparse(queryResults);
    }

    // 获取json格式查询结果
    public JSONObject getQueryResults(List<String> domains) {
        if (domains.size()> ApplicationConfig.API_BC_MAXIMUM_QUERYNUM){
            LOG.warn("Too many domains in a http post request, the number of fqdn/url should be no more than "
                    + ApplicationConfig.API_BC_MAXIMUM_QUERYNUM + "!");
        }
        JSONObject jsonRes = new JSONObject();
        try {
            URL url = new URL(ApplicationConfig.API_BC_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(ApplicationConfig.API_BC_METHOD);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json");

            JSONObject param = new JSONObject();
            param.put("oemid", ApplicationConfig.API_BC_OEMID);
            param.put("deviceid", ApplicationConfig.API_BC_DEVICEID);
            param.put("uid", ApplicationConfig.API_BC_UID);

            param.put("queries", Collections.singletonList(ApplicationConfig.API_BC_QUERYTYPE));
            param.put("a1cat", ApplicationConfig.API_BC_ISA1CAT);
            param.put("reputation", ApplicationConfig.API_BC_ISREPU);
            param.put("xml", ApplicationConfig.API_BC_ISXML);  // json or xml格式

            param.put("urls", domains);

            //建立实际的连接
            con.connect();
            OutputStreamWriter writer = new OutputStreamWriter(this.con.getOutputStream(), StandardCharsets.UTF_8);
            writer.write(param.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // 获取服务端响应，通过输入流来读取URL的响应
            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sbf = new StringBuilder();
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();

            jsonRes = JSONObject.parseObject(sbf.toString());
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonRes;
    }

    // json响应内容解析
    public List<DomainCategory> responseSparse(JSONObject records){
        List<DomainCategory> domainFiles = new ArrayList<>();
        Boolean querySucess = records.get("status").equals(200);

        if (!querySucess) {
            System.out.print(records);
            LOG.error("Wrong query. Query type: " + records.get("type"));
        } else {
            JSONArray array = records.getJSONArray("results");
            for (int i = 0; i < array.size(); i++) {
                JSONObject jo = array.getJSONObject(i);

                // json处理
                JSONObject queries = jo.getJSONObject("queries");
                JSONObject getInfo = queries.getJSONObject(ApplicationConfig.API_BC_QUERYTYPE);

                JSONObject cat = getInfo.getJSONArray("cats").getJSONObject(0);
                Integer catId = cat.getInteger("catid");
                String fqdn = jo.getString("url");
                domainFiles.add(new DomainCategory(
                        fqdn,
                        "brightcloud",
                        querySucess,
                        ValidationUtils.getMatchPattern(fqdn),
                        getInfo.getInteger("reputation"),
                        getRepLevel(getInfo.getInteger("reputation")),
                        catId,
                        getCatInfo(catId).get(0),
                        getCatInfo(catId).get(1),
                        cat.getInteger("conf"),
                        getInfo.getBoolean("a1cat")));
            }
        }
        return domainFiles;
    }

    private String getRepLevel(Integer repScore){
        String level = null;   //用str存放数据
        if (repScore > 80){ level="Trustworthy";}
        else if (repScore > 60){ level="Low Risk";}
        else if (repScore > 40){ level="Moderate Risk";}
        else if (repScore > 20){ level="Suspicious";}
        else if (repScore > 0){ level="High Risk";}
        return level;
    }

    // 获取类别id对应信息
    public void geneCatInfo(){
        if (catId2Info.size()==0){
            JSONObject jsonObject;
//            String filePath = Objects.requireNonNull(BrightCloud.class.getClassLoader()
//                    .getResource(ApplicationConfig.API_BC_CATEINFO_FILE)).getFile();
            String filePath =ApplicationConfig.API_BC_CATEINFO_FILE;
            String s = FileUtils.readJsonFile(filePath);
            jsonObject = JSON.parseObject(s);

            if (!(jsonObject==null)){
                JSONObject tmp = (JSONObject) jsonObject.getJSONArray("results").get(0);
                JSONArray catInfoArray = tmp.getJSONObject("queries").getJSONObject("getcatlist").getJSONArray("cats");

                for (int i = 0; i < catInfoArray.size(); i++){
                    JSONObject keyObject = catInfoArray.getJSONObject(i);
                    List<String> value = new ArrayList<>(Arrays.asList(
                            keyObject.getString("catname"),
                            keyObject.getString("catgroup")));
                    catId2Info.put(i+1, value);
                }
            }
        }
    }

    public List<String> getCatInfo(Integer catId){
        List<String> info = Arrays.asList("", "");

        if (0 < catId && catId <= 83) {
            if (catId2Info.size()==0){
                geneCatInfo();
            }

            info = catId2Info.get(catId);

            if (info == null){
                LOG.error("Failed at geneCatInfo function");
                System.out.print("Failed at geneCatInfo function");
            }
        }

        return info;
    }

    public Integer getMaxObjectNum() {
        return maxObjectNum;
    }

    public static void main(String[] args) {
        JSONObject queryResults = new BrightCloud().getQueryResults(Arrays.asList("baidu.com"));
        new BrightCloud().responseSparse(queryResults);
    }
}

