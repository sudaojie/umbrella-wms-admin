package com.ruoyi.wcs.util;

import com.ruoyi.wcs.constans.CronPatternConstant;
import com.ruoyi.wcs.enums.wcs.WcsPeriodEnum;
import com.ruoyi.wcs.enums.wcs.WcsWeekEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hewei
 * @date 2023/4/13 0013 09:34
 */
@Slf4j
public enum WcsGenerateCronUtil {
    /**
     * 单例
     */
    INSTANCE;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat weekFormat = new SimpleDateFormat("E");

    /**
     * 根据执行周期和初次执行时间，生成cron表达式
     *
     * @param period    执行周期
     * @param beginTime 初次执行时间
     * @return cron表达式
     */
    public String generateCronByPeriodAndTime(WcsPeriodEnum period, String beginTime) {
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(beginTime);
        } catch (ParseException e) {
            log.error("parse time error. [time]: {}", beginTime);
            return "";
        }
        String[] dateAndTime = beginTime.split(" ");
        String time = dateAndTime[0];
        String[] splitTime = time.split(":");
        String hour = splitTime[0];
        String minute = splitTime[1];
        String second = splitTime[2];
        String cron = "";
        switch (period) {
            case DAILY:
                cron = String.format(CronPatternConstant.DAILY_CRON_PATTERN, second, minute, hour);
                break;
            case WEEKLY:
                String week = weekFormat.format(parsedDate);
                String weekCode = WcsWeekEnum.nameOf(week).getCode();
                cron = String.format(CronPatternConstant.WEEKLY_CRON_PATTERN, second, minute, hour, weekCode);
                break;
            default:
                break;
        }
        return cron;
    }

        public static void main(String[] args) {
            String time = "19:21:05";
            String dailyCron = WcsGenerateCronUtil.INSTANCE.generateCronByPeriodAndTime(WcsPeriodEnum.DAILY, time);
            log.info("每天执行cron：" + dailyCron);
        }

}
