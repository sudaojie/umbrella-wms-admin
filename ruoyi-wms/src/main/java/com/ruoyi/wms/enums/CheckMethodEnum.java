package com.ruoyi.wms.enums;

/**
 * 盘点方式枚举类
 */
public enum CheckMethodEnum {

    NOT("0", "全盘"),
    ING("1", "部分盘");


    private String code;

    private String name;

    CheckMethodEnum(String code, String name) {
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

    public static CheckMethodEnum getInstance(String code) {
        for (CheckMethodEnum curEnum : CheckMethodEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
