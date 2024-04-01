package com.ruoyi.wms.enums;

/**
 * 出库单状态枚举类
 */
public enum OutBillEnum {
    WAIT("1", "待拣货"),
    PICKING("2", "拣货中"),
    ALREADY("3", "已出库"),
    CANCEL("4", "已作废");


    private String code;

    private String name;

    OutBillEnum(String code, String name) {
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

    public static OutBillEnum getInstance(String code) {
        for (OutBillEnum curEnum : OutBillEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}

