package com.ruoyi.wms.enums;

/**
 * 下架状态枚举类
 */
public enum OffShelfEnum {
    NOT("0", "未下架"),
    ALREADY("1", "已下架");


    private String code;

    private String name;

    OffShelfEnum(String code, String name) {
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

    public static OffShelfEnum getInstance(String code) {
        for (OffShelfEnum curEnum : OffShelfEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
