package com.mesasoft.cn.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yjy
 * @version 1.0
 * @date 2021/2/25 6:11 下午
 */
public class FileUtils {
    private static final Logger LOG = Logger.getLogger(FileUtils.class);

    public static List<String> readTxtFileIntoStringArrList(String filePath)
    {
        List<String> list = new ArrayList<>();
        try
        {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    if (!lineTxt.equals("")) {
                        list.add(lineTxt.trim());
                    }
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("Can not find file: " + filePath);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error occurred in Function 'readTxtFileIntoStringArrList'");
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> getBatchLineReadIn(BufferedReader bufferedReader, int batchSize){
        List<String> list = new ArrayList<>();
        String lineTxt;
        try{
            while ((lineTxt = bufferedReader.readLine()) != null && list.size()<batchSize)
            {
                if (!lineTxt.equals("")) {
                    list.add(lineTxt.trim());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    public static void createFile(File filePath, String fileName){
        try {
            File file = new File(filePath.toString() + "/" + fileName);

            if (!filePath.exists()){
                filePath.mkdirs();
            }

            boolean isCreate = file.createNewFile();
            if (isCreate){
                LOG.info("File " + fileName + " is created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createFile(File file){
        try {
            boolean isCreate = file.createNewFile();
            if (isCreate){
                LOG.info("File " + file + " is created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileName(File file){
        String[] tmp = file.toString().split("/");
        String fileName = tmp[tmp.length-1];
        return fileName;
    }


    public static void writerClose(OutputStreamWriter outWriter,  OutputStream outStream) throws IOException {
        assert outWriter != null;
        outWriter.close();
        outStream.close();
    }

    public static void readerClose(BufferedReader bufferedReader,  InputStreamReader inputStreamReader) throws IOException {
        assert inputStreamReader != null;
        bufferedReader.close();
        inputStreamReader.close();
    }

    //执行cmd命令，获取返回结果
    public static String execCMD(String command) {
        StringBuilder sb =new StringBuilder();
        try {
            Process process=Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line+"\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }

    public static Long getFileLineNum(File file){
        Long num = 0L;
        if (!file.exists()){
            LOG.error("File not exist: " + file.toString());
        } else {
            String res = FileUtils.execCMD("wc -l " + file.toString());
            num = Long.parseLong(res.trim().split(" ")[0]);
        }
        return num;
    }

    public static int getMaxLength(String filePath) {
        int lengthDomain = 0;
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String[] split = lineTxt.split("\\.");
                    if (split.length > lengthDomain) {
                        lengthDomain = split.length;
                    }
                }
                read.close();
            } else {
                LOG.error("FilePath is wrong--->{" + filePath + "}<---");
            }
        } catch (Exception e) {
            LOG.error("Get filePathData error--->{" + e + "}<---");
            e.printStackTrace();
        }
        return lengthDomain;
    }
}
