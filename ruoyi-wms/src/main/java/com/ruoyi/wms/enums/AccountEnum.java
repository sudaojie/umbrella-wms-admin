package com.ruoyi.wms.enums;

/**
 * 库存台账单据类型枚举类
 */
public enum AccountEnum {

    RKD("0", "入库单"),
    CKD("1", "出库单"),
    WDSJ("2", "无单上架"),
    WDXJ("3", "无单下架");


    private String code;

    private String name;

    AccountEnum(String code, String name) {
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

    public static AccountEnum getInstance(String code) {
        for (AccountEnum curEnum : AccountEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
