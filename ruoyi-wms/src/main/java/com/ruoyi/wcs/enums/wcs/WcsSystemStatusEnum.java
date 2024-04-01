package com.ruoyi.wcs.enums.wcs;

/**
 * WCS新风系统运行状态枚举
 */
public enum WcsSystemStatusEnum {


    NORMAL("0", "正常"),
    ABNORMAL("1", "异常"),
    ;


    private String code;

    private String name;


    WcsSystemStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsSystemStatusEnum getInstance(int code) {
        for (WcsSystemStatusEnum curEnum : WcsSystemStatusEnum.values()) {
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
