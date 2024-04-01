package com.ruoyi.wms.enums;

/**
 * 传输带库位朝向枚举类
 */
public enum TransferLocationArrowEnums {

    LEFT("1", "左侧"),
    RIGHT("2", "右侧");


    private String code;

    private String name;

    TransferLocationArrowEnums(String code, String name) {
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

    public static TransferLocationArrowEnums getInstance(String code) {
        for (TransferLocationArrowEnums curEnum : TransferLocationArrowEnums.values()) {
            if (curEnum.code.equals(code)) {
                return curEnum;
            }
        }
        return null;
    }

}
