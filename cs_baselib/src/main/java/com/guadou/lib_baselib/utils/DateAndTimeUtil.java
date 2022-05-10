package com.guadou.lib_baselib.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换工具类
 */

public class DateAndTimeUtil {

    /*
     * 将时间戳转换为时间 年月日
     */
    public static String stampToDate(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /*
     * PhP 时间戳转换
     */
    public static String stampToDate1(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 转换时间的另一种格式
     */
    public static String stampToDate2(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String stampToDate3(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String stampToDate4(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate5(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String stampToDate6(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate7(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 兼容php时间戳转换 yyyymmdd   只是自己数据库中查询保存的时候用
     */
    public static String stampToDate8(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 兼容php时间戳转换 yyyymm 只是自己数据库中查询保存的时候用
     */
    public static String stampToDate9(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /*
     * 将时间戳转换为时间 年月日
     */
    public static String stampToDate10(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间 星期 年月日
     */
    public static String stampToDate11(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间 星期 年月日
     */
    public static String stampToDate12(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /*
     * 将时间戳转换为时间 星期 年月日
     */
    public static String stampToDate13(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate14(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res.toLowerCase();
    }

    /*
     * 将时间戳转换为时间 星期 年月日
     */
    public static String stampToDate15(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String stampToDate19(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy EEE HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /*
     * 将时间戳转换为时间 星期 年月日
     */
    public static String stampToDate16(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate28(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM, yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String getYear(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate17(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate20(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public static String stampToDate21(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M.dd", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate22(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res.toLowerCase();
    }

    public static String stampToDate23(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" d MMM yyyy HH:mm", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate24(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res.toLowerCase();
    }

    public static String stampToDate25(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" dd MMM yyyy", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate26(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, MMMM dd", Locale.ENGLISH);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间 年月日
     */
    public static String stampToDate27(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDateNewsFeed(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }
        long lt = new Long(s);
        Date date = new Date(lt);

        String before;
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd, yyyy");
        before = simpleDateFormat1.format(date);

        String after;
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm a.");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
        after = simpleDateFormat2.format(date);
//        if (!after.contains("午")) {
//            //英文才加上点 中文不加点
//            after = after.substring(0, after.length() - 2) + "." + after.substring(after.length() - 2, after.length());
//        }
        return before + " at " + after.toLowerCase();
    }


    public static String getMonth(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String getMonthEN(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String getDay(String s) {
        //转换PHP时间戳
        if (s.length() < 11) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return 时间戳格式
     */
    public static long getTimeStamp(String timeStr, String format) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //日期转换成多少天前
    private static long minute = 1000 * 60;
    private static long hour = minute * 60;
    private static long day = hour * 24;
    private static long halfamonth = day * 15;
    private static long month = day * 30;
    private static long year = month * 12;

    public static String getDateDiff(String s) {

        if (s.length() < 13) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        Long dateTimeStamp = Long.parseLong(s);
        String result;
        long now = new Date().getTime();
        long diffValue = now - dateTimeStamp;
        if (diffValue < 0) {
            //toast("结束日期不能小于开始日期！");
        }
        long monthC = diffValue / month;
        long weekC = diffValue / (7 * day);
        long dayC = diffValue / day;
        long hourC = diffValue / hour;
        long minC = diffValue / minute;
        if (monthC >= 1) {
            result = Integer.parseInt(monthC + "") + " months ago";
            return result;
        } else if (weekC >= 1) {
            result = Integer.parseInt(weekC + "") + " weeks ago";
            return result;
        } else if (dayC >= 1) {
            result = Integer.parseInt(dayC + "") + " days ago";
            return result;
        } else if (hourC >= 1) {
            result = Integer.parseInt(hourC + "") == 1 ? "1 hour ago" : Integer.parseInt(hourC + "") + " hours ago";
            return result;
        } else if (minC >= 1) {
            result = Integer.parseInt(minC + "") == 1 ? "1 minute ago" : Integer.parseInt(minC + "") + " minutes ago";
            return result;
        } else {
            result = "a moment ago";
            return result;
        }
    }

    public static String getDiffYear(String s) {
        if (s.length() < 13) {
            StringBuilder builder = new StringBuilder(s);
            builder.append("000");
            s = builder.toString();
        }

        Long dateTimeStamp = Long.parseLong(s);
        String result;
        long now = new Date().getTime();
        long diffValue = now - dateTimeStamp;
        if (diffValue < 0) {
            //toast("结束日期不能小于开始日期！");
        }
        long years = diffValue / year;

        return String.valueOf(years);
    }

    /**
     * 计算时间差-转换为时分秒
     */
    public static String getDiffTimeMinutes(long mss) {

        String hour = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + "";
        String minutes = (mss % (1000 * 60 * 60)) / (1000 * 60) + "";
        String seconds = (mss % (1000 * 60)) / 1000 + "";
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return hour + ":" + minutes + ":" + seconds;
    }
}
