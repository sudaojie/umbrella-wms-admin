package com.ruoyi.wms.enums;

/**
 * 库区类型枚举类
 */
public enum LocationStatusEnum {

    HAVE_GOODS("0", "有货"),
    HAVE_TRAY("1", "有托盘无货"),
    NO_TRAY("2", "无托盘"),
    DISABLE("3", "库位禁用");

    private String code;

    private String name;

    LocationStatusEnum(String code, String name) {
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

    public static LocationStatusEnum getInstance(String code) {
        for (LocationStatusEnum curEnum : LocationStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
