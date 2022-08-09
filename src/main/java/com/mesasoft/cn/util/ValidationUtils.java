package com.mesasoft.cn.util;

import com.mesasoft.cn.sketch.api.BrightCloud;
import com.mesasoft.cn.sketch.config.ApplicationConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import sun.net.util.IPAddressUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Logger LOG = Logger.getLogger(ValidationUtils.class);

    /**
     * 获取二级域名
    **/
    public static String getSecDomain(String fqdnOrUrl){
        String filePath = Objects.requireNonNull(BrightCloud.class.getClassLoader()
                .getResource(ApplicationConfig.TLD_FILE)).getFile();
                HashMap<String, HashMap<String, String>> maps = readTopDomainFile(filePath);
        try {
            String[] split = fqdnOrUrl.split("\\.");
            String secDomain = null;
            for (int i = split.length - 1; i >= 0; i--) {
                int maps_index = split.length - (i + 1);
                HashMap<String, String> innerMap = maps.get("map_id_" + maps_index);
                HashMap<String, String> fullTop = maps.get("full");
                if (!(innerMap.containsKey(split[i]))) {
                    String strSec = "";
                    for (int j = i; j < split.length; j++) {
                        strSec += (split[j] + ".");
                    }
                    secDomain = strSec.substring(0, strSec.length() - 1);
                    if (fullTop.containsKey(getTopFromSecDomain(secDomain))) {
                        break;
                    } else {
                        while (!fullTop.containsKey(getTopFromSecDomain(secDomain)) && getTopFromSecDomain(secDomain).contains(".")) {
                            secDomain = getTopFromSecDomain(secDomain);
                        }
                        break;
                    }
                }
            }
            // 右匹配为顶级域名
            if (secDomain == null){
                secDomain = fqdnOrUrl;
            }
            return secDomain;
        } catch (Exception e) {
            LOG.error("urlDomain:" + fqdnOrUrl);
            e.printStackTrace();
            return "---no---return---";
        }
    }

    public static List<String> getSecDomain(List<String> fqdnOrUrls) {
        HashMap<String, HashMap<String, String>> maps = readTopDomainFile(ApplicationConfig.TLD_FILE);
        List<String> secDomainList = new ArrayList<>();
        for (String oriDomain : fqdnOrUrls) {
            String secDomain = getSecDomain(oriDomain);
            if (StringUtils.isNotBlank(secDomain) && !("---no---return---".equals(secDomain))) {
                secDomainList.add(secDomain);
            } else {
                System.out.println(oriDomain);
            }
        }
        return secDomainList;
    }

    public static String getTopFromSecDomain(String secDomain) {
        String quFirstDian = secDomain;
        if (secDomain.contains(".")) {
            quFirstDian = secDomain.substring(secDomain.indexOf(".")).substring(1);
        }
        return quFirstDian;
    }

    public static HashMap<String, HashMap<String, String>> readTopDomainFile(String filePath) {
        HashMap<String, HashMap<String, String>> maps = makeHashMap(filePath);
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    HashMap<String, String> fullTop = maps.get("full");
                    fullTop.put(lineTxt, lineTxt);
                    maps.put("full", fullTop);
                    String[] split = lineTxt.split("\\.");
                    for (int i = split.length - 1; i >= 0; i--) {
                        int maps_index = split.length - (i + 1);
                        HashMap<String, String> innerMap = maps.get("map_id_" + maps_index);
                        innerMap.put(split[i], split[i]);
                        maps.put("map_id_" + maps_index, innerMap);
                    }
                }
                read.close();
            } else {
                LOG.error("TopDomainUtils>=>readTopDomainFile filePath is wrong--->{" + filePath + "}<---");
            }
        } catch (Exception e) {
            LOG.error("TopDomainUtils>=>readTopDomainFile get filePathData error--->{" + e + "}<---");
            e.printStackTrace();
        }
        return maps;
    }

    public static HashMap<String, HashMap<String, String>> makeHashMap(String filePath) {
        int maxLength = FileUtils.getMaxLength(filePath);
        HashMap<String, HashMap<String, String>> maps = new HashMap<String, HashMap<String, String>>();
        for (int i = 0; i < maxLength; i++) {
            maps.put("map_id_" + i, new HashMap<String, String>());
        }
        maps.put("full", new HashMap<String, String>());
        return maps;
    }

    public static List<String> getChecked(List<String> objectList, String type){
        if (type.equals("ip")){
            return getCheckedIps(objectList);
        }
        if (type.equals("domain")){
            return getCheckedFqdns(objectList);
        }
        LOG.error("Wrong type to be checked: " + type);
        return objectList;
    }

    public static List<String> getCheckedFqdns(List<String> fqdns){
        List<String> res = new ArrayList<>();
        for (String fqdn:fqdns){
            //去端口号
            fqdn = fqdn.split(":")[0];
            // 去重 & 校验
            if (isValidDomain(fqdn) && !res.contains(fqdn)){
                res.add(fqdn.toLowerCase());
            } else {
                LOG.debug("Bad or duplicated fqdn:" + fqdn);
            }
        }
        return res;
    }

    public static List<String> getCheckedIps(List<String> ipList){
        List<String> res = new ArrayList<>();
        for (String ip:ipList){
            //去端口号
            ip = ip.split(":")[0];
            // 去重 & 校验
            if (isValidIp(ip) && !res.contains(ip)){
                res.add(ip.toLowerCase());
            } else {
                LOG.debug("Bad or duplicated fqdn:" + ip);
            }
        }
        return res;
    }

    public static  boolean isValidIp(String ip){
        boolean iPv4LiteralAddress = IPAddressUtil.isIPv4LiteralAddress(ip);
        boolean iPv6LiteralAddress = IPAddressUtil.isIPv6LiteralAddress(ip);
        return iPv4LiteralAddress || iPv6LiteralAddress;
    }

    private static boolean isValidDomain(String str)
    {
        String regex = "^((?!-)[A-Za-z0-9-_]"
                + "{1,63}(?<!-)\\.)"
                + "+[A-Za-z]{2,6}";
        Pattern p = Pattern.compile(regex);

        if (str == null) {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static Integer getMatchPattern(String fqdn){
        int match_pattern = 2;
        if (fqdn.equals(getSecDomain(fqdn))){
            match_pattern = 1;  // 二级域名-右匹配
        }
        return match_pattern;
    }




}
