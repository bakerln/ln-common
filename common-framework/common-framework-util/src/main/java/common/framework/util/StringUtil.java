package common.framework.util;

/**
 * <p>Description: String工具包，包装hutool工具
 *        包装String工具包，后期方便替换</p>
 *
 * @author linan
 * @date 2020-10-21
 */
public class StringUtil {

    /**
     * 判断字符串是null或空，null或""都返回true
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        return false == isNotNullOrEmpty(str);
    }


    /**
     * 判断字符串不是null或空
     *
     * @param str 字符串
     * @return
     */
    public static boolean isNotNullOrEmpty(String str) {
        if (str != null && !str.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
