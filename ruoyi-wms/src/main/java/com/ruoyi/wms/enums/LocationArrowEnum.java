package com.ruoyi.wms.enums;

/**
 * 库位朝向枚举
 */
public enum LocationArrowEnum {

    LEFT_ARROW("1", "左侧"),
    RIGHT_ARROW("2", "右侧");


    private String code;

    private String name;

    LocationArrowEnum(String code, String name) {
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

    public static LocationArrowEnum getInstance(String code) {
        for (LocationArrowEnum curEnum : LocationArrowEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

}
