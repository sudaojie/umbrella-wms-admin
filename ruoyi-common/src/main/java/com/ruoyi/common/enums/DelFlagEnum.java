package com.ruoyi.common.enums;

/**
 * 删除状态枚举类
 */
public enum DelFlagEnum {

    DEL_NO("0", "未删除"),
    DEL_YES("1", "已删除");


    private String code;

    private String name;

    DelFlagEnum(String code, String name) {
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

    public static DelFlagEnum getInstance(String code) {
        for (DelFlagEnum curEnum : DelFlagEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
