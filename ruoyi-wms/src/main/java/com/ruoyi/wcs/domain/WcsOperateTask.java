package com.ruoyi.wcs.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * WCS任务信息对象
 *
 * @author yangjie
 * @date 2023-02-28
 */
@Data
@Accessors(chain = true)
@TableName("wcs_operate_task")
public class WcsOperateTask extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 任务号
     */
    @Excel(name = "任务号")
    private String taskNo;

    /**
     * 开始库区
     */
    private String startAreaCode;


    /**
     * 结束库区
     */
    private String endAreaCode;


    /**
     * 业务任务号
     */
//    @Excel(name = "业务任务号")
    private String serviceTaskNo;


    /**
     * 任务设备类型(1.AVG 2.堆垛机)
     */
    @Excel(name = "任务设备类型")
    private String taskDeviceType;

    /**
     * 任务类型
     */
    @Excel(name = "任务类型")
    private String taskType;

    /**
     * 操作类型
     * takeTray:取盘
     * putTray:回盘
     * relocation:移库
     */
    @Excel(name = "操作类型")
    private String operateType;

    /**
     * 起始位置
     */
    @Excel(name = "起始位置")
    private String startPosition;

    /**
     * 目标位置
     */
    @Excel(name = "目标位置")
    private String endPosition;

    /**
     * 托盘号
     */
    @Excel(name = "托盘号")
    private String trayNo;

    /**
     * 执行开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Excel(name = "执行开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateBeginTime;

    /**
     * 执行结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Excel(name = "执行结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateEndTime;

    /**
     * 任务请求json
     */
    private String taskReqJson;

    /**
     * 任务列表请求json
     */
    private String waitTaskReqJson;


    /**
     * 任务响应json
     */
    private String taskRspJson;

    /**
     * 任务状态(1.未执行  2.执行中  3.执行成功 4.执行失败  5.人工中断)
     */
    @Excel(name = "任务状态")
    private String taskStatus;

    /**
     * 异常信息
     */
    private String errMsg;

    /**
     * 单据号
     */
    private String inBillNo;

}
