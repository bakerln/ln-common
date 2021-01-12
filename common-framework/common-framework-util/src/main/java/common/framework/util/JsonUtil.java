package common.framework.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * <p>Description: Json工具包，包装hutool工具
 *        包装JSON工具包，后期方便替换</p>
 *
 * @author linan
 * @date 2020-10-21
 */
public class JsonUtil {

    //------------------------------toString start--------------------------------------

    /**
     * 对象转Json字符串
     * @param object 对象
     * @return String 字符串
     */
    public static String toJsonStr(Object object){
        if (null == object) {
            return null;
        }else if (object instanceof String) {
            //避免二次转义
            return (String) object;
        }else if (object instanceof Collection){
            JSONArray jsonArray = JSONUtil.parseArray(object);
            String result  =  JSONUtil.toJsonStr(jsonArray);
            return result;
        }else {
            JSONObject jsonObject = JSONUtil.parseObj(object);
            String result  =  JSONUtil.toJsonStr(jsonObject);
            return result;
        }
    }

    /**
     * 对象转Json字符串
     * @param object 对象
     * @param ignoreNullValue 是否忽略空值
     * @return String 字符串
     */
    public static String toJsonStr(Object object,boolean ignoreNullValue){
        if (null == object) {
            return null;
        }else if (object instanceof String) {
            //避免二次转义
            return (String) object;
        }else if (object instanceof Collection){
            JSONArray jsonArray = JSONUtil.parseArray(object, ignoreNullValue);
            String result  =  JSONUtil.toJsonStr(jsonArray);
            return result;
        }else {
            JSONObject jsonObject = JSONUtil.parseObj(object,ignoreNullValue);
            String result  =  JSONUtil.toJsonStr(jsonObject);
            return result;
        }
    }

    /**
     *  对象转Json字符串 ,对Json串进行格式化，缩进4个单位
     * @param object 对象
     * @return String 字符串
     */
    public static String toJsonPrettyStr(Object object){
        if (null == object) {
            return null;
        }
        if (object instanceof String) {
            //避免二次转义
            return (String) object;
        }
        JSON json = JSONUtil.parse(object);
        String result = JSONUtil.toJsonPrettyStr(json);
        return result;
    }


    //------------------------------toString end--------------------------------------

    //------------------------------toBean start--------------------------------------

    /**
     *  JSON字符串转为实体类对象，转换异常将被抛出
     * @param json Json字符串
     * @param beanClass 实体类对象
     * @param <T> Bean类型
     * @return 实体类对象
     */
    public static <T> T toBean(String json,Class<T> beanClass){
        JSONObject jsonObject = JSONUtil.parseObj(json);
        return JSONUtil.toBean(jsonObject,beanClass);
    }

    /**
     *  JSON字符串转为实体类对象，转换异常将被抛出
     * @param json Json字符串
     * @param beanType 实体类对象类型
     * @param ignoreError 是否忽略错误
     * @param <T> Bean类型
     * @return 实体类对象
     */
    public static <T> T toBean(String json, Type beanType, boolean ignoreError ){
        JSONObject jsonObject = JSONUtil.parseObj(json);
        return JSONUtil.toBean(jsonObject,beanType,ignoreError);
    }

    /**
     *  将JSON字符串转换为Bean的List，默认为ArrayList
     * @param json Json字符串
     * @param elementType List中元素类型
     * @param <T> Bean类型
     * @return ArrayList
     */
    public static <T> List<T> toList(String json, Class<T> elementType){
        JSONArray jsonArray = JSONUtil.parseArray(json);
        return JSONUtil.toList(jsonArray,elementType);
    }

    //------------------------------toBean end--------------------------------------

    //------------------------------toOther start--------------------------------------

    /**
     *  Json字符串转XML格式字符串
     * @param json Json字符串
     * @return String XML格式字符串
     */
    public static String toXml(String json){
        JSON jsonParam = JSONUtil.parse(json);
        String result = XML.toXml(jsonParam);
        return result;
    }

    /**
     * XML格式字符串转Json字符串
     * @param xml XML格式字符串
     * @return string Json字符串
     */
    public static String xmlToJson(String xml){
        JSONObject jsonObject = JSONUtil.parseFromXml(xml);
        return JSONUtil.toJsonStr(jsonObject);
    }


//------------------------------toOther end--------------------------------------


    /**
     * 通过表达式获取JSON中嵌套的对象<br>
     * <ol>
     * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
     * <li>[]表达式，可以获取集合等对象中对应index的值</li>
     * </ol>
     *
     * 表达式栗子：
     * map中有resultWrapper,以及一个array对象
     *
     * <pre>
     * resultWrapper
     * resultWrapper.msg
     * array[1]
     *
     * person.friends[5].name
     * </pre>
     *
     * @param json {@link JSON}
     * @param expression 表达式
     * @return 对象
     * @see JSON#getByPath(String)
     */
    public static Object getByPath(String json, String expression) {
        JSON jsonObject = JSONUtil.parse(json);
        return (null == json || StrUtil.isBlank(expression)) ? null : jsonObject.getByPath(expression);
    }


    /**
     * 设置表达式指定位置（或filed对应）的值<br>
     * 若表达式指向一个JSONArray则设置其坐标对应位置的值，若指向JSONObject则put对应key的值<br>
     * 注意：如果为JSONArray，则设置值得下标不能大于已有JSONArray的长度<br>
     * <ol>
     * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
     * <li>[]表达式，可以获取集合等对象中对应index的值</li>
     * </ol>
     *
     * 表达式栗子：
     *
     * <pre>
     * persion
     * persion.name
     * persons[3]
     * person.friends[5].name
     * </pre>
     *
     * @param json JSON，可以为JSONObject或JSONArray
     * @param expression 表达式
     * @param value 值
     */
    public static String putByPath(String json, String expression, Object value) {
        JSON jsonObject = JSONUtil.parse(json);
        jsonObject.putByPath(expression, value);
        return JSONUtil.toJsonStr(jsonObject);
    }


    /**
     * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
     * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
     * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
     *
     * @param string 字符串
     * @return 适合在JSON中显示的字符串
     */
    public static String quote(String string) {
        return JSONUtil.quote(string, true);
    }

    /**
     * 格式化JSON字符串，此方法并不严格检查JSON的格式正确与否
     *
     * @param jsonStr JSON字符串
     * @return 格式化后的字符串
     */
    public static String formatJsonStr(String jsonStr) {
        return JSONUtil.formatJsonStr(jsonStr);
    }

}


