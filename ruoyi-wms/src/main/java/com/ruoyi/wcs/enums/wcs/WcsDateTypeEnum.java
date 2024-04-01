package com.ruoyi.wcs.enums.wcs;

/**
 * WCS统计日期类型枚举
 */
public enum WcsDateTypeEnum {


    DAY("1", "日"),
    WEEK("2", "周"),
    MONTH("3", "月"),
    YEAR("4", "年"),
    HALF_MONTH("5", "半月"),
    FIVE_DAYS("6", "近五天"),
    THREE_DAYS("7", "近三天"),
    ONE_DAY("8", "近一天"),
    THIS_WEEK("9", "周"),
    ;


    private String code;

    private String name;


    WcsDateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsDateTypeEnum getInstance(int code) {
        for (WcsDateTypeEnum curEnum : WcsDateTypeEnum.values()) {
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
