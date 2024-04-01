package com.ruoyi.wcs.constans;

/**
 * @author hewei
 * @date 2023/4/13 0013 09:33
 */
public class CronPatternConstant {
    /**
     * 执行单次cron表达式模板
     * eg: 59 59 23 1 12 ? 2022 (2022-12-01 23:59:59执行一次)
     */
    public static final String ONCE_CRON_PATTERN = "%s %s %s %s %s ? %s";
    /**
     * 每天执行cron表达式模板
     * eg: 59 59 23 * * ? (每日23:59:59执行)
     */
    public static final String DAILY_CRON_PATTERN = "%s %s %s * * ?";
    /**
     * 每周执行cron表达式模板
     * eg: 59 59 23 ? * Fri (每周五23:59:59执行)
     */
    public static final String WEEKLY_CRON_PATTERN = "%s %s %s ? * %s";
    /**
     * 每月执行cron表达式模板
     * eg: 59 59 23 8 * ? (每月8号23:59:59执行)
     */
    public static final String MONTHLY_CRON_PATTERN = "%s %s %s %s * ?";

}
