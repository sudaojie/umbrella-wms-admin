package com.ruoyi.wms.enums;

/**
 * 设备主控IO搜索类型枚举
 */
public enum MainCtrlSearchTypeEnum {

    LOCATION_CODE_TYPE("1", "库位号搜索"),
    TRAY_CODE_TYPE("2", "托盘号搜索"),
    PART_CODE_TYPE("3", "机件号搜索");

    private String code;

    private String name;

    MainCtrlSearchTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
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

    public static MainCtrlSearchTypeEnum getInstance(String code) {
        for (MainCtrlSearchTypeEnum curEnum : MainCtrlSearchTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
