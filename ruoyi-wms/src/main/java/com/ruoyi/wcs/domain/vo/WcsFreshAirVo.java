package com.ruoyi.wcs.domain.vo;

import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author hewei
 * @date 2023/4/12 0012 11:44
 */
@Data
@Accessors(chain = true)
public class WcsFreshAirVo extends WcsFreshAirDetailInfo implements Serializable {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备区域
     */
    private String deviceArea;

    /**
     * 温度范围
     */
    private String temperatureArrange;

    /**
     * 湿度范围
     */
    private String humidityArrange;

    private String tMax;

    private String tMarks;

    private String hMax;

    private String hMarks;

    /**
     * 是否选择
     */
    private Boolean choose;

    /**
     * 新风编号
     */
    private String refreshAirId;

}
