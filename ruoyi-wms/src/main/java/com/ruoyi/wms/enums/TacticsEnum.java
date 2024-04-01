package com.ruoyi.wms.enums;

/**
 * 策略枚举类
 */
public enum TacticsEnum {
    AVERAGE("0", "平均分配"),
    CONCENTRATE("1", "集中堆放");


    private String code;

    private String name;

    TacticsEnum(String code, String name) {
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

    public static TacticsEnum getInstance(String code) {
        for (TacticsEnum curEnum : TacticsEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
