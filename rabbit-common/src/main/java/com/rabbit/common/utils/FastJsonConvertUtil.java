package com.rabbit.common.utils;

import com.alibaba.fastjson.JSON;

/**
 * @author Evan
 * @create 2021/2/23 15:03
 */
public class FastJsonConvertUtil {

    public static <T> T convertJsonToObject(String data, Class<T> clazz){
        try {
            return JSON.parseObject(data, clazz);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String convertObjectToJson(Object object){
        return JSON.toJSONString(object);
    }

}
