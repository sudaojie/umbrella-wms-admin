package com.ruoyi.wms.enums;

/**
 * 盘点状态枚举类
 */
public enum CheckStatusEnum {

    NOT("0", "未开始"),
    ING("1", "盘点中"),
    ALREADY("2", "已完成");


    private String code;

    private String name;

    CheckStatusEnum(String code, String name) {
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

    public static CheckStatusEnum getInstance(String code) {
        for (CheckStatusEnum curEnum : CheckStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
