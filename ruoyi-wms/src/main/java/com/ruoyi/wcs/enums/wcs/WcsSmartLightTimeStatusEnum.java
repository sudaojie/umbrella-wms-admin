package com.ruoyi.wcs.enums.wcs;

/**
 * WCS照明系统执行时间类型枚举
 */
public enum WcsSmartLightTimeStatusEnum {


    START("0", "开始"),
    END("1", "结束"),
    ;


    private String code;

    private String name;


    WcsSmartLightTimeStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsSmartLightTimeStatusEnum getInstance(int code) {
        for (WcsSmartLightTimeStatusEnum curEnum : WcsSmartLightTimeStatusEnum.values()) {
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
