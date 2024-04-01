package com.ruoyi.wms.enums;

/**
 * 入库单号前缀枚举类
 */
public enum InBillNoPrefixEnum {

    QCRK("1", "期初入库", "QCRK"),
    PTRK("2", "普通入库", "PTRK"),
    PYRK("3", "盘盈入库", "PYRK"),
    LSRK("4", "晾晒入库", "LSRK"),
    QTRK("5", "其他入库", "QTRK");


    private String code;

    private String name;

    private String prefix;

    InBillNoPrefixEnum(String code, String name, String prefix) {
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

    public static InBillNoPrefixEnum getInstance(String code) {
        for (InBillNoPrefixEnum curEnum : InBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

    public static String getPrefix(String code) {
        for (InBillNoPrefixEnum curEnum : InBillNoPrefixEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum.prefix;
            }
        }
        return "RK";
    }
}
