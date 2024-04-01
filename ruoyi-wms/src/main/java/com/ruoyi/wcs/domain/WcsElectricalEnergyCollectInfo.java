package com.ruoyi.wcs.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

/**
 * wcs电能能耗信息采集对象
 *
 * @author hewei
 * @date 2023-04-10
 */
@Data
@Accessors(chain = true)
@TableName("wcs_electrical_energy_collect_info")
public class WcsElectricalEnergyCollectInfo implements Serializable {


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
     * 通讯地址
     */
    @Excel(name = "通讯地址")
    private String postalAddress;

    /**
     * 总电能
     */
    private Double totalElectricalEnergy;

    /**
     * 有功总电能
     */
    @Excel(name = "有功总电能")
    private Double activeTotalElectricalEnergy;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectTime;


}
