package com.ruoyi.wcs.enums.stacker;

/**
 * 堆垛机任务类型枚举
 */
public enum StackerTaskTypeEnum {

    IN_STOCK(1, "入库"),
    OUT_STOCK(2, "出库"),
    MOVE_STOCK(3, "移库"),
    INVENTORY_STOCK(5, "盘库"),
    BACK_STOCK(8, "回库")
    ;


    private int code;

    private String name;


    StackerTaskTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static StackerTaskTypeEnum getInstance(int code) {
        for (StackerTaskTypeEnum curEnum : StackerTaskTypeEnum.values()) {
            if (curEnum.code == code) {
                return curEnum;
            }
        }
        return null;
    }


}
