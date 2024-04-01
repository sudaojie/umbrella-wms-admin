package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * 智慧照明系统详情信息对象
 *
 * @author hewei
 * @date 2023-04-12
 */
@Data
@Accessors(chain = true)
@TableName("wcs_smart_lighting_detail_info")
public class WcsSmartLightingDetailInfo implements Serializable {


    /**
     * 编号
     */
    private String id;

    /**
     * 照明设备编号
     */
    @Excel(name = "照明设备编号")
    private String deviceInfoId;

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

    /**
     * 开始时间
     */
    @Excel(name = "开始时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date beginTime;

    /**
     * 结束时间
     */
    @Excel(name = "结束时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date endTime;

}
