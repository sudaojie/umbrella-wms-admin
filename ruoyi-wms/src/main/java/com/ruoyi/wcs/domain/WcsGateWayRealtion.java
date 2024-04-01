package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WCS网关设备关联关系对象
 *
 * @author yangjie
 * @date 2023-03-31
 */
@Data
@Accessors(chain = true)
@TableName("wcs_gate_way_realtion")
public class WcsGateWayRealtion extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 网关设备编号
     */
    @Excel(name = "网关设备编号")
    private String gateWayDeviceNo;

    /**
     * 除agv 堆垛机 网关设备编号
     */
    @Excel(name = "除agv 堆垛机 网关设备编号")
    private String noIpDeviceNo;

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
     * 关联温湿度传感器数量
     */
    @TableField(exist = false)
    private String templateureNum;

    /**
     * 关联烟雾监测传感器数量
     */
    @TableField(exist = false)
    private String smokeNum;

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
