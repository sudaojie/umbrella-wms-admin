package com.ruoyi.common.enums;

/**
 * 打印状态枚举类
 */
public enum PrintStatusEnum {

    PRINT_NO("0", "未打印"),
    PRINT_YES("1", "已打印");


    private String code;

    private String name;

    PrintStatusEnum(String code, String name) {
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

    public static PrintStatusEnum getInstance(String code) {
        for (PrintStatusEnum curEnum : PrintStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
