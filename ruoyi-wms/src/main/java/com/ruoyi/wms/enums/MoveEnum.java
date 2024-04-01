package com.ruoyi.wms.enums;

/**
 * 上架状态枚举类
 */
public enum MoveEnum {
    unexecuted("0", "待执行"),
    executing("1", "执行中"),
    executed("2", "已完成");


    private String code;

    private String name;

    MoveEnum(String code, String name) {
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

    public static MoveEnum getInstance(String code) {
        for (MoveEnum curEnum : MoveEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
