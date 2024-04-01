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
 * 新风系统温湿度采集信息对象
 *
 * @author hewei
 * @date 2023-04-12
 */
@Data
@Accessors(chain = true)
@TableName("wcs_fresh_air_collect_info")
public class WcsFreshAirCollectInfo implements Serializable {

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


}
