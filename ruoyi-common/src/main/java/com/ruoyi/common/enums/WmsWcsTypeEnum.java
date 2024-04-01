package com.ruoyi.common.enums;

/**
 * WmsWcs交互类型枚举类
 */
public enum WmsWcsTypeEnum {
    TAKETRAY("takeTray", "取盘"),
    PUTTRAY("putTray", "回盘"),
    RELOCATION("relocation", "移库");

    private String code;

    private String name;

    WmsWcsTypeEnum(String code, String name) {
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

    public static WmsWcsTypeEnum getInstance(String code) {
        for (WmsWcsTypeEnum curEnum : WmsWcsTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
