package com.ruoyi.wms.enums;

/**
 * 盘点类型枚举类
 */
public enum CheckTypeEnum {

    LOCATION("0", "库位"),
    GOODS("1", "货物类型");


    private String code;

    private String name;

    CheckTypeEnum(String code, String name) {
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

    public static CheckTypeEnum getInstance(String code) {
        for (CheckTypeEnum curEnum : CheckTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
