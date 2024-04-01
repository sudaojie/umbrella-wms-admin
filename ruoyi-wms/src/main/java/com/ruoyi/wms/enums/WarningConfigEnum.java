package com.ruoyi.wms.enums;

/**
 * 库存预警枚举类
 */
public enum WarningConfigEnum {

    VALIDITY("wms_validity", "有效期预警"),
    DETAINED("wms_detained", "滞压期预警");


    private String code;

    private String name;

    WarningConfigEnum(String code, String name) {
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

    public static WarningConfigEnum getInstance(String code) {
        for (WarningConfigEnum curEnum : WarningConfigEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
