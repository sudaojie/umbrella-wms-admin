package com.ruoyi.wcs.enums.agv;

/**
 * 劢微Agv 系统状态响应码枚举
 */
public enum AgvSysRespCodeEnum {

    UnKnow(0, "未知状态"),
    Normal(1, "系统正常");


    private int code;

    private String name;


    AgvSysRespCodeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static AgvSysRespCodeEnum getInstance(int code) {
        for (AgvSysRespCodeEnum curEnum : AgvSysRespCodeEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }

}
