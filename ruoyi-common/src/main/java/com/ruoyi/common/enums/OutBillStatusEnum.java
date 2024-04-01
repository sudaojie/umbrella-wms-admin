package com.ruoyi.common.enums;

/**
 * 删除状态枚举类
 */
public enum OutBillStatusEnum {

    WAIT("1", "待拣货"),
    OUTPROCESS("2", "拣货中"),
    ENDSTATUS("3", "已出库"),
    CANCEL("4", "已作废");


    private String code;

    private String name;

    OutBillStatusEnum(String code, String name) {
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

    public static OutBillStatusEnum getInstance(String code) {
        for (OutBillStatusEnum curEnum : OutBillStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
