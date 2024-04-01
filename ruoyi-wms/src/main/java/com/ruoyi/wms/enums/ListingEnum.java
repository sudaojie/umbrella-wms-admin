package com.ruoyi.wms.enums;

/**
 * 上架状态枚举类
 */
public enum ListingEnum {
    NOT("0", "未上架"),
    ALREADY("1", "已上架"),
    ING("2", "上架中"),
    ERROR("3", "上架失败");


    private String code;

    private String name;

    ListingEnum(String code, String name) {
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

    public static ListingEnum getInstance(String code) {
        for (ListingEnum curEnum : ListingEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
