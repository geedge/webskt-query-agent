package com.mesasoft.cn.util;

import java.util.regex.Pattern;

public class Verify {


    private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

    private static Pattern pDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);

    public static boolean domainValid(String domainName) {
        return pDomainName.matcher(domainName).find();
    }

    public static void main(String[] args) {
        boolean b = domainValid("192.168.44.12");
        System.err.println(b);
    }
}
