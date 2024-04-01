package com.ruoyi.wcs.enums.agv;

/**
 * 劢微Agv 小车状态码
 */
public enum AgvSmallCarRespCodeEnum {

    UNKNOW(0, "未知状态"),
    NOTRUN(1, "没有运行"),
    RUNING(2, "正在运行（任务中）"),
    OBSTACLE(3, "避障状态"),
    RAPIDSTOP(5, "急停状态"),
    ABNORMAL_PICKUP(6, "取货异常叉车取货未找到货物"),
    ABNORMAL_DELIVERY(7, "放货异常尖部光电避障"),
    ;

    private int code;

    private String name;


    AgvSmallCarRespCodeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static AgvSmallCarRespCodeEnum getInstance(int code) {
        for (AgvSmallCarRespCodeEnum curEnum : AgvSmallCarRespCodeEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }

}
