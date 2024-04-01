package com.ruoyi.wms.enums;

/**
 * 取盘状态枚举类
 */
public enum TakeTrayStatusEnum {
    NOT("0", "未取盘"),
    ALREADY("1", "已取盘"),
    ING("2", "取盘中");


    private String code;

    private String name;

    TakeTrayStatusEnum(String code, String name) {
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

    public static TakeTrayStatusEnum getInstance(String code) {
        for (TakeTrayStatusEnum curEnum : TakeTrayStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
