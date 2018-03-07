package com.luotuo.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * Created by liuma on 2017/8/8.
 */
public class JsonUtils {

    /* 将json字符串转换成List<Map>集合 */
    public static List convertJson2List(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> list = null;
        try {
            list = objectMapper.readValue(json, List.class);
        } catch (JsonParseException e) {
            throw e;
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return list;
    }
    public static Map<String, String> convertJson2Map(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map  = objectMapper.readValue(json, Map.class);
        } catch (JsonParseException e) {
            throw e;
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return map;
    }

    /* JavaBean(Entity/Model)转换成JSON */
    public static String writeEntityJSON(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        String res = null;
        try {
            res = objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            return "json字符串转换失败";
        }
        return res;
    }

    public static Map<String, String> convertEntity2Map(Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        Map<String, String> map = null;
        try {
            json = objectMapper.writeValueAsString(obj);
            map  = objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw e;
        }
        return map;
    }

    public static <T> T jsonStr2Object(String content, Class<T> cls) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        T obj = objectMapper.readValue(content, cls);
        return obj;
    }


    /*将json字符串转换成List<T>集合*/
    public static List json2List(String json) throws Exception {
        if (json == null || json.equals(""))
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> list = objectMapper.readValue(json, new TypeReference<List<Object>>() {
        });
        return list;
    }
}
