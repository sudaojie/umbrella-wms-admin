package com.ruoyi.wcs.constans;

/**
 * @author Administrator
 * @description 照明系统定时任务常量类
 */
public class WcsTimerConstants {

    /**
     * 照明系统-全局定时开启任务名称
     */
    public static final String START_TASK_NAME = "照明系统-全局定时开启任务";

    /**
     * 照明系统-全局定时关闭任务名称
     */
    public static final String FINISH_TASK_NAME = "照明系统-全局定时关闭任务";

    /**
     * 定时任务开启状态
     */
    public static final String JOB_START_STATUS = "0";

    /**
     * 定时任务关闭状态
     */
    public static final String JOB_CLOSE_STATUS = "1";

    /**
     * 定时任务禁止并发状态
     */
    public static final String NON_CONCURRENT = "1";

    /**
     * 定时任务默认组名称
     */
    public static final String DEFAULT_GROUP_NAME = "DEFAULT";

    /**
     * 定时任务计划策略 立即触发执行
     */
    public static final String IN_TIME_EXECUTE_STATUS = "1";

    /**
     * 全局定时任务调用目标字符串 开启
     */
    public static final String START_INVOKE_TARGET = "WcsLightStartBatchTask.startBatchTask('{}')";

    /**
     * 全局定时任务调用目标字符串 关闭
     */
    public static final String FINISH_INVOKE_TARGET = "WcsLightEndBatchTask.endBatchTask('{}')";

    /**
     * 单个定时任务调用目标字符串 开启
     */
    public static final String SINGLE_BEGIN_INVOKE_TARGET = "WcsLightStartTask.startTask('{}')";

    /**
     * 单个定时任务调用目标字符串 关闭
     */
    public static final String SINGLE_END_INVOKE_TARGET = "WcsLightEndTask.endTask('{}')";

    /**
     * 日期格式化
     */
    public static final String DATE_PATTERN = "HH:mm:ss";

    /**
     * 照明系统-单个定时开启任务名称
     */
    public static final String START_SINGLE_TASK_NAME = "照明系统-{}定时开启任务";

    /**
     * 照明系统-单个定时关闭任务名称
     */
    public static final String FINISH_SINGLE_TASK_NAME = "照明系统-{}定时关闭任务";

}
