package com.ruoyi.wms.enums;

/**
 * 入库单号前缀枚举类
 */
public enum ListingNoPrefixEnum {

    SJ("1", "上架", "SJ");


    private String code;

    private String name;

    private String prefix;

    ListingNoPrefixEnum(String code, String name, String prefix) {
        this.code = code;
        this.name = name;
        this.prefix = prefix;
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

    public static ListingNoPrefixEnum getInstance(String code) {
        for (ListingNoPrefixEnum curEnum : ListingNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

    public static String getPrefix() {
        return SJ.prefix;
    }
}
