package com.ruoyi.wcs.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

/**
 * wcs电压电流信息采集对象
 *
 * @author ruoyi
 * @date 2023-04-10
 */
@Data
@Accessors(chain = true)
@TableName("wcs_power_collect_info")
public class WcsPowerCollectInfo implements Serializable {


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
     * 总有功功率(kw)
     */
    @Excel(name = "总有功功率(kw)")
    private String totalActivePower;

    /**
     * A相有功功率
     */
    @Excel(name = "A相有功功率")
    private String phaseActivePowerA;

    /**
     * B相有功功率
     */
    @Excel(name = "B相有功功率")
    private String phaseActivePowerB;

    /**
     * C相有功功率
     */
    @Excel(name = "C相有功功率")
    private String phaseActivePowerC;

    /**
     * 总无功功率(kvarh)
     */
    @Excel(name = "总无功功率(kvarh)")
    private String totalReactivePower;

    /**
     * A相无功功率
     */
    @Excel(name = "A相无功功率")
    private String phaseReactivePowerA;

    /**
     * B相无功功率
     */
    @Excel(name = "B相无功功率")
    private String phaseReactivePowerB;

    /**
     * C相无功功率
     */
    @Excel(name = "C相无功功率")
    private String phaseReactivePowerC;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectTime;


}
