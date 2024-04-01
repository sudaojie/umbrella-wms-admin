package com.ruoyi.wms.enums;

/**
 * 出库状态枚举
 */
public enum OutStatusEnum {

    NOT_OUT("0", "未出库"),
    HAS_OUT("1", "已出库")
    ;

    private String code;

    private String name;

    OutStatusEnum(String code, String name) {
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

    public static OutStatusEnum getInstance(String code) {
        for (OutStatusEnum curEnum : OutStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
