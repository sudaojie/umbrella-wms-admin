package com.ruoyi.wms.enums;

/**
 * 上架单状态枚举类
 */
public enum GoodsInfoEnum {
    ENABLE("0", "启用"),
    DISABLE("1", "禁用");


    private String code;

    private String name;

    GoodsInfoEnum(String code, String name) {
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

    public static GoodsInfoEnum getInstance(String code) {
        for (GoodsInfoEnum curEnum : GoodsInfoEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
