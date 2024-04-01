package com.ruoyi.wcs.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

/**
 * 设备预警信息对象
 *
 * @author hewei
 * @date 2023-04-17
 */
@Data
@Accessors(chain = true)
@TableName("wcs_device_early_warning_info")
public class WcsDeviceEarlyWarningInfo implements Serializable {

    /**
     * 编号
     */
    private String id;
    /**
     * 设备编号
     */
    private String deviceInfoId;

    /**
     * 预警内容
     */
    @Excel(name = "预警内容")
    private String warningContent;

    /**
     * 预警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date warningTime;

}
