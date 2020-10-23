package common.framework.util.jsonutil;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.util.List;
import java.util.Map;

/**
 * <p>Description: Json工具包，包装hutool工具 </p>
 * 包装JSON工具包，后期方便替换
 * @author linan
 * @date 2020-10-21
 */
public class JsonUtil {

    /**
     * 对象转Json字符串
     * @param object 对象
     * @param clazz  数据类型
     * @return string 字符串
     */
    public static String toJsonStr(Object object, Class clazz){
        JSON json = JSONUtil.parse(object);
        return JSONUtil.toJsonStr(object);
    }

    public static List toList(List list,Class clazz){
        return null;
    }

    public static Map toMap(){
        return null;
    }

    public static Object toBean(){
        return null;
    }

    public static String xmlToJson(){
        return null;
    }

    public static boolean isJson(){
        return true;
    }
}


