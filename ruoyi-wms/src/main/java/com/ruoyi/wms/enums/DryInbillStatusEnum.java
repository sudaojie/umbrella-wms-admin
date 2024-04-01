package com.ruoyi.wms.enums;


/**
 * 晾晒入库类型枚举类
 */
public enum DryInbillStatusEnum {

    WAIT("0", "待入库"),
    GROUPIN("1", "组盘中"),
    TAKE("2", "已组盘"),
    ING("3", "入库中"),
    END("4", "已入库");

    private String code;

    private String name;

    DryInbillStatusEnum(String code, String name) {
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

    public static DryInbillStatusEnum getInstance(String code) {
        for (DryInbillStatusEnum curEnum : DryInbillStatusEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
