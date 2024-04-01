package com.ruoyi.wcs.enums.wcs;

/**
 * WCS任务设备类型枚举
 */
public enum WcsEnergyTreeTypeEnum {


    WAREHOUSE("1", "仓库"),
    AREA("2", "库区"),
    DEVICE("3", "设备"),
    ;


    private String code;

    private String name;


    WcsEnergyTreeTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsEnergyTreeTypeEnum getInstance(int code) {
        for (WcsEnergyTreeTypeEnum curEnum : WcsEnergyTreeTypeEnum.values()) {
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
