package com.ruoyi.common.enums;

/**
 * WmsWcs任务类型枚举类
 */
public enum WmsWcsTaskTypeEnum {
    NORMAL_WAREHOUSING("0", "正常入库"),
    NORMAL_EMPTYTRAY("8", "回空盘"),
    NORMAL_OUTBOUND("1", "正常出库"),
    DRY_STORAGE("2", "晾晒入库"),
    DRYING_OUTBOUND("3", "晾晒出库"),
    MOVE_THE_LIBRARY("4", "移库"),
    NO_ORDER("5", "无单"),
    CHECK_WAREHOUSEING("6", "盘点入库"),
    CHECK_OUTBOUND("7", "盘点出库");

    private String code;

    private String name;

    WmsWcsTaskTypeEnum(String code, String name) {
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

    public static WmsWcsTaskTypeEnum getInstance(String code) {
        for (WmsWcsTaskTypeEnum curEnum : WmsWcsTaskTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
