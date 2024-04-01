package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WCS新风温湿度传感器关联关系对象
 *
 * @author yangjie
 * @date 2023-03-31
 */
@Data
@Accessors(chain = true)
@TableName("wcs_fresh_air_thtb_realtion")
public class WcsFreshAirThtbRealtion extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 新风设备编号
     */
    @Excel(name = "新风设备编号")
    private String freshAirDeviceNo;

    /**
     * 温湿度传感器设备编号
     */
    @Excel(name = "温湿度传感器设备编号")
    private String thtbDeviceNo;

    /**
     * 旧设备编号 （业务处理）
     */
    @TableField(exist = false)
    private String oldDeviceNo;

    /**
     * 关联设备数量
     */
    @TableField(exist = false)
    private Long num;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String initDeviceName;

    /**
     * 关联设备名称
     */
    @TableField(exist = false)
    private String relateDeviceName;

}
