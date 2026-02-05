package com.htmake.reader.utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonPath工具类
 */
@Slf4j
public class JsonPathUtils {

    /**
     * 根据JsonPath获取字符串
     */
    public static String getString(String json, String path) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return "";
        }

        try {
            Object result = JsonPath.read(json, path);
            if (result == null) {
                return "";
            }
            return result.toString();
        } catch (PathNotFoundException e) {
            log.debug("JsonPath未找到: {}", path);
            return "";
        } catch (Exception e) {
            log.error("JsonPath解析失败: {}", path, e);
            return "";
        }
    }

    /**
     * 根据JsonPath获取字符串列表
     */
    public static List<String> getStringList(String json, String path) {
        List<String> result = new ArrayList<>();

        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return result;
        }

        try {
            Object obj = JsonPath.read(json, path);
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                for (Object item : list) {
                    if (item != null) {
                        result.add(item.toString());
                    }
                }
            } else if (obj != null) {
                result.add(obj.toString());
            }
        } catch (PathNotFoundException e) {
            log.debug("JsonPath未找到: {}", path);
        } catch (Exception e) {
            log.error("JsonPath解析失败: {}", path, e);
        }

        return result;
    }

    /**
     * 根据JsonPath获取对象
     */
    public static Object get(String json, String path) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return null;
        }

        try {
            return JsonPath.read(json, path);
        } catch (PathNotFoundException e) {
            log.debug("JsonPath未找到: {}", path);
            return null;
        } catch (Exception e) {
            log.error("JsonPath解析失败: {}", path, e);
            return null;
        }
    }

    /**
     * 判断路径是否存在
     */
    public static boolean exists(String json, String path) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return false;
        }

        try {
            JsonPath.read(json, path);
            return true;
        } catch (PathNotFoundException e) {
            return false;
        } catch (Exception e) {
            log.error("JsonPath解析失败: {}", path, e);
            return false;
        }
    }
}
