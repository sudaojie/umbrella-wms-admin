package com.ruoyi.wcs.enums.wcs;

/**
 * WCS任务运行状态枚举
 */
public enum WcsTaskRunStatusEnum {


    NOT_STARTED("0", "未开始"),
    IN_PROGRESS("1", "执行中"),
    COMPLETED("2", "执行成功"),
    IN_EXCEPTION("3", "执行失败"),
    FORCED_INTERRUPT("4", "人工中断")
    ;


    private String code;

    private String name;


    WcsTaskRunStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsTaskRunStatusEnum getInstance(int code) {
        for (WcsTaskRunStatusEnum curEnum : WcsTaskRunStatusEnum.values()) {
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
