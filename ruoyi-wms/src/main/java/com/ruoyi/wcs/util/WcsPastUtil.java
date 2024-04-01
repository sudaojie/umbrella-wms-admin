package com.ruoyi.wcs.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author hewei
 * @date 2023/2/16 0016 10:54
 * @description 获取过去指定几天的日期列表
 */
public class WcsPastUtil {

    public static void main(String[] args) {
    }

    public static Date getDateAdd(int days) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    /**
     * 获取指定几天日期
     *
     * @param days days
     * @return result result
     */
    public static List<String> getDaysBetween(int days) {
        List<String> result = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(getDateAdd(days));
        Long startTIme = start.getTimeInMillis();
        Calendar end = Calendar.getInstance();
        end.setTime(new Date());
        Long endTime = end.getTimeInMillis();
        Long oneDay = 1000 * 60 * 60 * 24L;
        Long time = startTIme;
        while (time <= endTime) {
            Date d = new Date(time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            result.add(df.format(d));
            time += oneDay;
        }
        return result;
    }

    /**
     * 获取当前日期时间
     *
     * @return timestamp
     */
    public static String getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 获取七天前的日期
     *
     * @return timestamp
     */
    public static String getIntervalSevenTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取指定两个日期间的日期列表
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @param type      type
     * @return list
     */
    public static ArrayList<String> getAllDateByParamDate(String startDate, String endDate, String type) {
        if (!"year".equals(type) && !"month".equals(type) && !"day".equals(type)) {
            return null;
        }
        DateTime parseStartDate = DateUtil.parse(startDate);
        DateTime parseEndDate = DateUtil.parse(endDate);
        // 存储所有日期的list
        ArrayList<String> list = new ArrayList<>();
        // 获取所有年份
        if (type.equals("year")) {
            list.add(parseStartDate.toString("yyyy"));
            DateTime endDateOffset = parseEndDate.offsetNew(DateField.YEAR, -1);
            while (parseStartDate.isBefore(endDateOffset)) {
                DateTime stageDateTime = parseStartDate.offset(DateField.YEAR, 1);
                list.add(stageDateTime.toString("yyyy"));
                parseStartDate = stageDateTime;
            }
        } else if (type.equals("month")) {
            // 获取所有月份
            list.add(parseStartDate.toString("yyyy-MM"));
            DateTime endDateOffset = parseEndDate.offsetNew(DateField.MONTH, -1);
            while (parseStartDate.isBefore(endDateOffset)) {
                DateTime stageDateTime = parseStartDate.offset(DateField.MONTH, 1);
                list.add(stageDateTime.toString("yyyy-MM"));
                parseStartDate = stageDateTime;
            }
        } else {
            // 获取所有日期
            list.add(parseStartDate.toString("yyyy-MM-dd"));
            DateTime endDateOffset = parseEndDate.offsetNew(DateField.DAY_OF_MONTH, -1);
            while (parseStartDate.isBeforeOrEquals(endDateOffset)) {
                DateTime stageDateTime = parseStartDate.offset(DateField.DAY_OF_MONTH, 1);
                list.add(stageDateTime.toString("yyyy-MM-dd"));
                parseStartDate = stageDateTime;
            }
        }
        // 返回数据
        return list;
    }


}
