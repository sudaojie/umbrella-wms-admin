package com.ruoyi.wcs.enums.wcs;

/**
 * WCS任务设备类型枚举
 */
public enum WcsTaskTypeEnum {


    IN_STORE("1", "入库"),
    OUT_STORE("2", "出库"),
    MOVE_STORE("3", "移库"),
    ;


    private String code;

    private String name;


    WcsTaskTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsTaskTypeEnum getInstance(int code) {
        for (WcsTaskTypeEnum curEnum : WcsTaskTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
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

}
