package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * 智慧照明系统定时任务关系对象
 *
 * @author hewei
 * @date 2023-04-13
 */
@Data
@Accessors(chain = true)
@TableName("wcs_smart_lighting_job_relate_info")
public class WcsSmartLightingJobRelateInfo implements Serializable {


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
     * 定时任务编号
     */
    @Excel(name = "定时任务编号")
    private String jobId;

    /**
     * 0-单个执行 1-全局执行
     */
    @Excel(name = "0-单个执行 1-全局执行")
    private Integer type;

    /**
     * 0-开始 1-结束
     */
    @Excel(name = "0-开始 1-结束")
    private Integer status;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 全局执行开启或关闭标志位
     */
    private Integer batchOpenCloseFlag;

}
