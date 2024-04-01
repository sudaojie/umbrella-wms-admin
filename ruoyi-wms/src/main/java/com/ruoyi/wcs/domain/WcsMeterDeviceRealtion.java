package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WCS电表设备关联关系对象
 *
 * @author ruoyi
 * @date 2023-05-10
 */
@Data
@Accessors(chain = true)
@TableName("wcs_meter_device_realtion")
public class WcsMeterDeviceRealtion extends BaseEntity{

    /**
     * 编号
     */
    private String id;

    /**
     * 电表编号
     */
    private String meterDeviceNo;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 旧设备编号 （业务处理）
     */
    @TableField(exist = false)
    private String oldDeviceNo;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String initDeviceName;

    /**
     * 关联堆垛机数量
     */
    @TableField(exist = false)
    private String stackerNum;

    /**
     * 关联照明传感器数量
     */
    @TableField(exist = false)
    private String lightNum;

    /**
     * 关联电表传感器数量
     */
    @TableField(exist = false)
    private String ammeterNum;

    /**
     * 关联新风设备数量
     */
    @TableField(exist = false)
    private String freshNum;

}
