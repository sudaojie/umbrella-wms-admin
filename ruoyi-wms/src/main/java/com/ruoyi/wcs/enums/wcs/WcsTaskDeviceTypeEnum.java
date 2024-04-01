package com.ruoyi.wcs.enums.wcs;

/**
 * WCS任务设备类型枚举
 */
public enum WcsTaskDeviceTypeEnum {


    AVG("1", "AGV"),
    STACKER("2", "堆垛机"),
    TEMPERATURE_AND_HUMIDITY("3", "温湿度传感器"),
    SMOKE("4", "烟雾监测传感器"),
    LIGHT("5", "照明传感器"),
    AMMETER("6", "电表传感器"),
    FRESHAIR("7", "新风"),
    GATEWAY("8", "网关采集器"),
    CAMERA("9", "摄像头"),
    DEHUMIDIFIER("10", "除湿机"),
    ;


    private String code;

    private String name;


    WcsTaskDeviceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static WcsTaskDeviceTypeEnum getInstance(int code) {
        for (WcsTaskDeviceTypeEnum curEnum : WcsTaskDeviceTypeEnum.values()) {
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
