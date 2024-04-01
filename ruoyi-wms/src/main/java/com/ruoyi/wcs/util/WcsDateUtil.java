package com.ruoyi.wcs.util;

import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * @author hewei
 * @date 2023/4/10 0010 16:41
 */
public class WcsDateUtil {
    public static ThreadLocal<DateFormat> chinaDateSDF = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return df;

        }
    };

    /**
     * 获取日期yyyy-MM-dd格式字符串
     *
     * @param date
     * @param locale
     * @return
     */
    public static String dateToStr(Date date, Locale locale) {
        if (locale == null) {

        }
        return chinaDateSDF.get().format(date);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 返回相差的天数
     */
    public static int differentDaysByString(String startDate, String endDate) {
        int days = 0;
        try {
            days = (int) ((Objects.requireNonNull(parseDate(endDate)).getTime() - Objects.requireNonNull(parseDate(startDate)).getTime()) / (1000 * 3600 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 日期格式转换
     *
     * @param date 日期
     * @return 日期格式
     */
    public static Date parseDate(String date) throws ParseException {
        if (date.isEmpty()) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    @SneakyThrows
    public static long dateDiff(String startTime, String endTime, String format) {
        SimpleDateFormat sd = new SimpleDateFormat(format);

        long nd = 1000 * 24 * 60 * 60;
        //一天的毫秒数
        long nh = 1000 * 60 * 60;
        //一小时的毫秒数
        long nm = 1000 * 60;
        //一分钟的毫秒数
        long ns = 1000;
        //一秒钟的毫秒数

        long diff;
        //获得两个时间的毫秒时间差异
        diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
//        long day = diff / nd;
        //计算差多少天
        return diff % nd / nh;
        //计算差多少小时
//        long min = diff % nd % nh / nm;
        //计算差多少分钟
//        long sec = diff % nd % nh % nm / ns;
        //计算差多少秒//输出结果
//        System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
    }

    /**
     * 获取当前日期的本周一是几号
     *
     * @return 本周一的日期
     */
    public static Date getThisWeekMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 获得当前日期是一个星期的第几天 使用cal.get(Calendar.DAY_OF_WEEK);
        //获取的数表示的是每个星期的第几天，不能改变，其中星期日为第一天
        // 如果是星期日则获取天数时获取到的数字为1 在后面进行相减的时候出错
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //  cal.getFirstDayOfWeek()根据前面的设置 来动态的改变此值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.format(WcsDateUtil.getThisWeekMonday(), "yyyy-MM-dd"));
        System.out.println(WcsDateUtil.dateDiff("00:00", "23:00", "HH:ss"));
    }
}
