package com.ruoyi.wms.enums;

/**
 * 库区类型枚举类
 */
public enum AreaTypeEnum {

    CCQ("0", "存储区"),
    LSQ("1", "晾晒区"),
    LHQ("2", "理货区");


    private String code;

    private String name;

    AreaTypeEnum(String code, String name) {
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

    public static AreaTypeEnum getInstance(String code) {
        for (AreaTypeEnum curEnum : AreaTypeEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
