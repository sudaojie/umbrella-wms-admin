package com.ruoyi.wms.enums;

/**
 * 出库货物单状态枚举类
 */
public enum OutBillGoodsEnum {
    WAIT("0", "待拣货"),
    TAKEN("1", "已取出"),
    PICKED("2", "已拣货");


    private String code;

    private String name;

    OutBillGoodsEnum(String code, String name) {
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

    public static OutBillGoodsEnum getInstance(String code) {
        for (OutBillGoodsEnum curEnum : OutBillGoodsEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
