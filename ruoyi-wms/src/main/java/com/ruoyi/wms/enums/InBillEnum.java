package com.ruoyi.wms.enums;

/**
 * 入库单状态枚举类
 */
public enum InBillEnum {

    WAIT("1", "待收货"),
    INSPECTED("2", "已收货"),
    PUTONING("3","上架中"),
    PUTONED("4","已上架"),
    REPEAL("5","已作废");


    private String code;

    private String name;

    InBillEnum(String code, String name) {
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

    public static InBillEnum getInstance(String code) {
        for (InBillEnum curEnum : InBillEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
