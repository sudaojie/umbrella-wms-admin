package com.ruoyi.wcs.domain.vo;

import com.ruoyi.wcs.domain.WcsTemplatureHumidityCollectInfo;
import lombok.Data;

import java.util.List;

@Data
public class WcsStatisticsVo {

    /**
     * 温湿度
     */
    private List<WcsTemplatureHumidityCollectInfo> list;

    /**
     * 晾晒
     */
    private List<WcsDryOutHourVo> result;

    /**
     * 区域类型
     */
    private String deviceArea;

    /**
     * 数量
     */
    private String num;

    /**
     * 时间
     */
    private String time;

}
