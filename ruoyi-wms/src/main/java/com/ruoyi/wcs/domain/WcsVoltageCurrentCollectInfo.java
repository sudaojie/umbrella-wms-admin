package com.ruoyi.wcs.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * wcs电压电流信息采集对象
 *
 * @author hewei
 * @date 2023-04-10
 */
@Data
@Accessors(chain = true)
@TableName("wcs_voltage_current_collect_info")
public class WcsVoltageCurrentCollectInfo implements Serializable {


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
     * A相电压(V)
     */
    @Excel(name = "A相电压(V)")
    private String phaseVoltageA;

    /**
     * B相电压(V)
     */
    @Excel(name = "B相电压(V)")
    private String phaseVoltageB;

    /**
     * C相电压(V)
     */
    @Excel(name = "C相电压(V)")
    private String phaseVoltageC;

    /**
     * A相电流(A)
     */
    @Excel(name = "A相电流(A)")
    private String phaseCurrentA;

    /**
     * B相电流(A)
     */
    @Excel(name = "B相电流(A)")
    private String phaseCurrentB;

    /**
     * C相电流(A)
     */
    @Excel(name = "C相电流(A)")
    private String phaseCurrentC;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectTime;


}
