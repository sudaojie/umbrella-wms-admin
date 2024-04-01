package com.ruoyi.wcs.enums.wcs;

/**
 * WCS新风系统运行状态枚举
 */
public enum WcsSwitchStatusEnum {


    CLOSE("0", "关闭"),
    OPEN("1", "运行"),
    ;


    private String code;

    private String name;


    WcsSwitchStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsSwitchStatusEnum getInstance(int code) {
        for (WcsSwitchStatusEnum curEnum : WcsSwitchStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
