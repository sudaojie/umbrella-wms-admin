package com.ruoyi.wms.api.enums;

/**
 * 堆垛机到位类型
 */
public enum StackerSignalEnum {

    IN_GOODS_SIGNAL("1", "入库货物到位"),
    OUT_TRANSFER_SIGNAL("2", "出库货物传输带到位"),
    MOVE_STORE_SIGNAL("3", "移库到位");

    private String code;

    private String name;

    StackerSignalEnum(String code, String name) {
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

    public static StackerSignalEnum getInstance(String code) {
        for (StackerSignalEnum curEnum : StackerSignalEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
