package com.ruoyi.wms.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 堆垛机报警信息上报 传输对象
 */
@Data
public class StackerWarnReportDto {

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * 堆垛机编号
     */
    private String stackerId;


    /**
     * 报警原因
     */
    private String alarmReason;

    /**
     * 报警编码
     */
    private String alarmCode;

    /**
     * 报警时间
     */
    private Date alarmTime;

}
