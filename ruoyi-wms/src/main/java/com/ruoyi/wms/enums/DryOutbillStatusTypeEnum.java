package com.ruoyi.wms.enums;

/**
 * 晾晒出库类型枚举类
 */
public enum DryOutbillStatusTypeEnum {

    WAIT("0", "待出库"),
    IN("1", "出库中"),
    END("2", "已出库");


    private String code;

    private String name;

    DryOutbillStatusTypeEnum(String code, String name) {
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

    public static DryOutbillStatusTypeEnum getInstance(String code) {
        for (DryOutbillStatusTypeEnum curEnum : DryOutbillStatusTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
