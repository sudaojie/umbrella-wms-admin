package com.ruoyi.wms.enums;

public enum AdjustStatusEnum {
    wait("0", "待处理"),
    end("1", "已完成");


    private String code;

    private String name;

    AdjustStatusEnum(String code, String name) {
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

    public static AdjustStatusEnum getInstance(String code) {
        for (AdjustStatusEnum curEnum : AdjustStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
