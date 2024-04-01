package com.ruoyi.wms.enums;

/**
 * 盘点单号前缀枚举类
 */
public enum CheckBillNoPrefixEnum {

    QBPD("1", "全部盘点", "QBPD"),
    HWPD("2", "部分货位盘点", "HWPD"),
    LXPD("3", "部分货物盘点", "LXPD");


    private String code;

    private String name;

    private String prefix;

    CheckBillNoPrefixEnum(String code, String name, String prefix) {
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

    public static CheckBillNoPrefixEnum getInstance(String code) {
        for (CheckBillNoPrefixEnum curEnum : CheckBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

    public static String getPrefix(String code) {
        for (CheckBillNoPrefixEnum curEnum : CheckBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum.prefix;
            }
        }
        return "CK";
    }
}
