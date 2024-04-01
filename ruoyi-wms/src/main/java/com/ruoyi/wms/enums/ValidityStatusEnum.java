package com.ruoyi.wms.enums;

/**
 * 晾晒出库类型枚举类
 */
public enum ValidityStatusEnum {

    WAIT("0", "即将过期"),
    EXPIRED("1", "已过期");


    private String code;

    private String name;

    ValidityStatusEnum(String code, String name) {
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

    public static ValidityStatusEnum getInstance(String code) {
        for (ValidityStatusEnum curEnum : ValidityStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
