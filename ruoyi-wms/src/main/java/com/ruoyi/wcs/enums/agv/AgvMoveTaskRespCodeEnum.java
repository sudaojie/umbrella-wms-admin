package com.ruoyi.wcs.enums.agv;

/**
 * 劢微Agv 搬运任务状态码
 */
public enum AgvMoveTaskRespCodeEnum {


    NOT_RUN(0, "没有运行"),
    CARRIER_RUN(4, "车辆启动"),
    ACTIONCOMPLETED_ONOUTPOS(5, "完成取货"),
    ACTIONCOMPLETED_ONINPOS(6, "完成放货"),
    SUCCESS(7, "任务完成")
    ;


    private int code;

    private String name;


    AgvMoveTaskRespCodeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static AgvMoveTaskRespCodeEnum getInstance(int code) {
        for (AgvMoveTaskRespCodeEnum curEnum : AgvMoveTaskRespCodeEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }
}
