package com.ruoyi.wms.enums;

/**
 * 库位类型枚举
 */
public enum LocationTypeEnum {

    PARENT_LOCATION_TYPE("1", "母库位"),
    CHILD_LOCATION_TYPE("2", "子库位");


    private String code;

    private String name;

    LocationTypeEnum(String code, String name) {
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

    public static LocationTypeEnum getInstance(String code) {
        for (LocationTypeEnum curEnum : LocationTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

}
