package com.ruoyi.common.enums;

/**
 * 启用状态
 */
public enum EnableStatus {

    ENABLE("0", "启用"),
    DISABLE("1", "禁用");


    private String code;

    private String name;

    EnableStatus(String code, String name) {
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

}
