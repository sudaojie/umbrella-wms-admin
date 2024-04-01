package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.io.Serializable;

/**
 * 新风系统详情信息对象
 *
 * @author hewei
 * @date 2023-04-12
 */
@Data
@Accessors(chain = true)
@TableName("wcs_fresh_air_detail_info")
public class WcsFreshAirDetailInfo implements Serializable {


    /**
     * 编号
     */
    private String id;

    /**
     * 新风设备编号
     */
    @Excel(name = "新风设备编号")
    private String deviceInfoId;

    /**
     * 当前温度
     */
    @Excel(name = "当前温度")
    private String templature;

    /**
     * 当前湿度
     */
    @Excel(name = "当前湿度")
    private String humidity;

    /**
     * 温度设置低位
     */
    @Excel(name = "温度设置低位")
    private String templatureLow;

    /**
     * 温度设置高位
     */
    @Excel(name = "温度设置高位")
    private String templatureHigh;

    /**
     * 湿度设置低位
     */
    @Excel(name = "湿度设置低位")
    private String humidityLow;

    /**
     * 湿度设置高位
     */
    @Excel(name = "湿度设置高位")
    private String humidityHigh;

    /**
     * 系统状态(0-正常 1-异常)
     */
    @Excel(name = "系统状态(0-正常 1-异常)")
    private Integer systemStatus;

    /**
     * 开关状态(0-关 1-开)
     */
    @Excel(name = "开关状态(0-关 1-开)")
    private Integer switchStatus;

}
