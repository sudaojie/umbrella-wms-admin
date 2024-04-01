package com.ruoyi.wms.enums;

/**
 * 是否为空的枚举类
 */
public enum IsEmptyEnum {
    ISEMPTY("0", "空的"),
    NOTEMPTY("1", "非空的");


    private String code;

    private String name;

    IsEmptyEnum(String code, String name) {
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

    public static IsEmptyEnum getInstance(String code) {
        for (IsEmptyEnum curEnum : IsEmptyEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
