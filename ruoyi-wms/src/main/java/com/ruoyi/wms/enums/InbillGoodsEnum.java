package com.ruoyi.wms.enums;

/**
 * 机件号状态枚举类
 */
public enum InbillGoodsEnum {
    NOT("0", "未打印"),
    ALREADY("1", "已打印");


    private String code;

    private String name;

    InbillGoodsEnum(String code, String name) {
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

    public static InbillGoodsEnum getInstance(String code) {
        for (InbillGoodsEnum curEnum : InbillGoodsEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
