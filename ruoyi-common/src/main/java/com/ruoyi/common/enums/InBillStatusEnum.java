package com.ruoyi.common.enums;

/**
 * WmsWcs交互类型枚举类
 */
public enum InBillStatusEnum {
    ONE("1", "待收货"),
    TOW("2", "已收货"),
    THREE("3", "上架中"),
    FOUR("4","已上架"),
    FIVE("5","已作废");

    private String code;

    private String name;

    InBillStatusEnum(String code, String name) {
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

    public static InBillStatusEnum getInstance(String code) {
        for (InBillStatusEnum curEnum : InBillStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
