package com.ruoyi.wcs.enums.wcs;

/**
 * WCS照明系统执行类型枚举
 */
public enum WcsSmartLightRunStatusEnum {


    SINGLE("0", "单个执行"),
    MULTI("1", "全局执行"),
    ;


    private String code;

    private String name;


    WcsSmartLightRunStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsSmartLightRunStatusEnum getInstance(int code) {
        for (WcsSmartLightRunStatusEnum curEnum : WcsSmartLightRunStatusEnum.values()) {
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
