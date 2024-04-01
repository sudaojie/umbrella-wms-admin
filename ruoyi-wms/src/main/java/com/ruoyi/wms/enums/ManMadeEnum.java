package com.ruoyi.wms.enums;

/**
 * 人工状态枚举类
 */
public enum ManMadeEnum {
    NOT("0", "未执行"),
    ALREADY("1", "已执行");


    private String code;

    private String name;

    ManMadeEnum(String code, String name) {
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

    public static ManMadeEnum getInstance(String code) {
        for (ManMadeEnum curEnum : ManMadeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
