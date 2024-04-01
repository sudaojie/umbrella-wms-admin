package com.ruoyi.wcs.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

/**
 * 温湿度采集数据信息对象
 *
 * @author ruoyi
 * @date 2023-05-08
 */
@Data
@Accessors(chain = true)
@TableName("wcs_templature_humidity_collect_info")
public class WcsTemplatureHumidityCollectInfo {


    /**
     * 编号
     */
    private String id;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String deviceInfoId;

    /**
     * 温度
     */
    @Excel(name = "温度")
    private String templature;

    /**
     * 湿度
     */
    @Excel(name = "湿度")
    private String humidity;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectTime;

    public WcsTemplatureHumidityCollectInfo() {
    }

    public WcsTemplatureHumidityCollectInfo(String templature, String humidity, Date collectTime) {
        this.templature = templature;
        this.humidity = humidity;
        this.collectTime = collectTime;
    }
}
