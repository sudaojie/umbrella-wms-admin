package com.ruoyi.wcs.domain.vo;

import lombok.Data;

@Data
public class WcsTemplatureHumidityMonitorVo {

    /**
     * 平均温度
     */
    private String averageTemplature;

    /**
     * 平均湿度
     */
    private String averageHumidity;

    /**
     * 温度同占比
     */
    private String temperatureRatio;

    /**
     * 湿度同占比
     */
    private String humidityRatio;

    /**
     * 温度 上升-下降标识位
     */
    private String templatureFlag;

    /**
     * 湿度 上升-下降标识位
     */
    private String humidityFlag;

}
