package com.rabbit.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * @author Evan
 * @create 2021/3/4 14:28
 */
@Slf4j
public class Props {

    enum Silent{ON, OFF};

    public static Properties tryProperties(String propertiesFileName, String userHomeBasePath){
        Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = tryResource(propertiesFileName, userHomeBasePath, Silent.ON);
            if (inputStream != null){
                properties.load(inputStream);
            }
        } catch (IOException e) {
            log.error("Load properties error : {}", e.getMessage());
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
        return properties;
    }

    public static InputStream tryResource(String propertiesFileName, String userHomeBasePath,
                                          Silent silent){
        InputStream inputStream = currentDirResource(new File(propertiesFileName));
        if (inputStream != null){
            return inputStream;
        }

        inputStream = userHomeResource(propertiesFileName, userHomeBasePath);
        if (inputStream != null){
            return inputStream;
        }

        inputStream = classPathResource(propertiesFileName);
        if (inputStream != null || silent == Silent.ON){
            return inputStream;
        }

        throw new RuntimeException("Failed to find " + propertiesFileName
                + " in current dir or classpath");
    }

    private static InputStream currentDirResource(File file){
        if (!file.exists()){
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Read file {} error.", file, e);
        }
        return null;
    }

    private static InputStream userHomeResource(String propertiesFileName, String userHomeBasePath){
        String filePath = System.getProperty("user.home") + File.separator + userHomeBasePath;
        File dir = new File(filePath);
        if (!dir.exists()){
            return null;
        }
        return currentDirResource(new File(dir, propertiesFileName));
    }

    public static InputStream classPathResource(String propertiesFileName){
        return Props.class.getClassLoader().getResourceAsStream(propertiesFileName);
    }

}
