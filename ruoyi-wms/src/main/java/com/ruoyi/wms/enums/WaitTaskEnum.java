package com.ruoyi.wms.enums;

/**
 * 待执行任务状态枚举类
 */
public enum WaitTaskEnum {

    NOT("0", "未执行"),
    ALREADY("1", "已执行");


    private String code;

    private String name;

    WaitTaskEnum(String code, String name) {
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

    public static WaitTaskEnum getInstance(String code) {
        for (WaitTaskEnum curEnum : WaitTaskEnum.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }
}
