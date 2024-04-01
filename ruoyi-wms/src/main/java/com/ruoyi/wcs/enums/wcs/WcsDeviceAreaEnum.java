package com.ruoyi.wcs.enums.wcs;

/**
 * WCS设备区域枚举
 * @author hewei
 */
public enum WcsDeviceAreaEnum {


    STORAGE("0", "存储"),
    DRY("1", "晾晒"),
    TALLY("2", "理货"),
    ;


    private String code;

    private String name;


    WcsDeviceAreaEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsDeviceAreaEnum getInstance(int code) {
        for (WcsDeviceAreaEnum curEnum : WcsDeviceAreaEnum.values()) {
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
