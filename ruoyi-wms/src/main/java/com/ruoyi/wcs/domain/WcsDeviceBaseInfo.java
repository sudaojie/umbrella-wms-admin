package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WCS设备基本信息对象
 *
 * @author yangjie
 * @date 2023-02-24
 */
@Data
@Accessors(chain = true)
@TableName("wcs_device_base_info")
public class WcsDeviceBaseInfo extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String deviceNo;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 设备点位
     */
    @Excel(name = "设备点位")
    private String devicePosition;

    /**
     * 库区编号
     */
    private String warehouseAreaCode;

    /**
     * 设备类型(1.AGV  2.堆垛机  3.温湿度传感器  4.烟雾监测传感器  5.照明传感器  6.电表传感器)
     */
    @Excel(name = "设备类型",dictType = "device_type")
    private String deviceType;


    /**
     * 设备区域
     */
    @Excel(name = "设备区域",dictType = "wms_area_type")
    private String deviceArea;

    /**
     * 摄像头通道号
     */
    @Excel(name = "摄像头通道号")
    private String channelId;

    /**
     * 设备地址码(AGV、堆垛机类型设备除外)
     */
    private String deviceAddress;

    /**
     * 设备IP
     */
    @Excel(name = "设备IP")
    @TableField(insertStrategy = FieldStrategy.IGNORED,updateStrategy = FieldStrategy.IGNORED)
    private String deviceIp;

    /**
     * 设备端口
     */
    @Excel(name = "设备端口")
    @TableField(insertStrategy = FieldStrategy.IGNORED,updateStrategy = FieldStrategy.IGNORED)
    private Long devicePort;

    /**
     * 设备规格
     */
    @Excel(name = "设备规格")
    private String deviceSize;

    /**
     * 设备厂家
     */
    @Excel(name = "设备厂家")
    private String deviceProducer;

    /**
     * 启用状态
     */
    private String enableStatus;

    /**
     * 摄像头封面
     */
    @TableField(exist = false)
    private String img;

    /**
     * 摄像头推流地址
     */
    @TableField(exist = false)
    private String url;

    /**
     * 设备图片路径
     */
    private String deviceImagePath;

    /**
     * 温度
     */
    private String templature;

    /**
     * 湿度
     */
    private String humidity;

    /**
     * 0-无烟 1-有烟
     */
    private String smokeFlag;

    /**
     * 新风系统状态 0-关闭 1-开启
     */
    @TableField(exist = false)
    private Integer freshAirStatus;

    /**
     * 照明系统状态 0-关闭 1-开启
     */
    @TableField(exist = false)
    private Integer lightStatus;

    /**
     * 线圈数量 适用于新风照明除湿机
     */
     private String coilAddress;

    /**
     * 堆垛机运行状态（0.正常 1.故障）
     */
    private String runningStatus;

}
