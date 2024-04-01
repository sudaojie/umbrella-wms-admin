package com.ruoyi.wms.enums;

/**
 * 入库单号前缀枚举类
 */
public enum OutBillNoPrefixEnum {

    ZCCK("1", "正常出库", "ZCCK"),
    DBCK("2", "调拨出库", "DBCK"),
    BSCK("3", "报损出库", "BSCK"),
    PKCK("4", "盘亏出库", "PKCK"),
    LSCK("5", "晾晒出库", "LSCK");


    private String code;

    private String name;

    private String prefix;

    OutBillNoPrefixEnum(String code, String name, String prefix) {
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

    public static OutBillNoPrefixEnum getInstance(String code) {
        for (OutBillNoPrefixEnum curEnum : OutBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

    public static String getPrefix(String code) {
        for (OutBillNoPrefixEnum curEnum : OutBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum.prefix;
            }
        }
        return "CK";
    }
}
