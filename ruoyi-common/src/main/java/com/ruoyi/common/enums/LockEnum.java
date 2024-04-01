package com.ruoyi.common.enums;

/**
 * 锁定状态枚举类
 */
public enum LockEnum {
    NOTLOCK("0", "未锁定"),
    LOCKED("1", "已锁定");


    private String code;

    private String name;

    LockEnum(String code, String name) {
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

    public static LockEnum getInstance(String code) {
        for (LockEnum curEnum : LockEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
