package com.ruoyi.wms.api.enums;

/**
 * AGV到位类型
 */
public enum AgvSignalEnum {

    IN_SIGNAL("1", "入库上架至传输带到位"),
    OUT_SIGNAL("2", "出库下架至规划库位到位");


    private String code;

    private String name;

    AgvSignalEnum(String code, String name) {
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

    public static AgvSignalEnum getInstance(String code) {
        for (AgvSignalEnum curEnum : AgvSignalEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
