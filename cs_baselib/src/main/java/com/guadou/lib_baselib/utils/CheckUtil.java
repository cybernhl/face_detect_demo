package com.guadou.lib_baselib.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

    private CheckUtil() {
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、187、188</p>
     *               <p>联通的号段：130、131、132、155、156、185、186</p>
     *               <p>电信的号段：133、153、180、189</p>
     * @return 验证成功返回true，验证失败返回false
     * <p>
     * Android 自带Email和MobileMun的检测
     */
    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3456789]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 判断字符串是否是数字带小数点
     */
    public static boolean checkNumberPoint(String str) {

        if (CheckUtil.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
        Matcher matcher = pattern.matcher((CharSequence) str);
        return matcher.matches();
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     * <p>
     * Android也有Url地址的检测
     */
    public static boolean checkURL(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
//        URLUtil.isValidUrl()
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmpty(Object[] os) {
        return isNull(os) || os.length == 0;
    }

    public static boolean isEmpty(Collection<?> l) {
        return isNull(l) || l.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return isNull(m) || m.isEmpty();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    /**
     * 抽象出的方法 用来得到验证结果 参数 0 是空 -1不匹配 1是匹配
     *
     * @param pattern_str  正则
     * @param inputContent 输入的内容
     * @return
     */
    private static int patternInt(String pattern_str, String inputContent) {
        Pattern p = Pattern.compile(pattern_str);
        Matcher m = p.matcher(inputContent);

        if (TextUtils.isEmpty(inputContent)) {
            return 0;
        } else if (!m.matches()) {
            return -1;
        }
        return 1;
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 9位数，第一个和最后一个为字母，中间7位是数字。
     *               确认使用
     * @return 验证成功返回true，验证失败返回false
     */
    public static int checkIdCard(String idCard) {
        String str = "[a-zA-Z]\\d{7}[a-zA-Z]{1}";
        return patternInt(str, idCard);
    }

}

