package com.ruoyi.wcs.req.stacker;

import lombok.Data;

/**
 * 堆垛机任务请求对象
 */
@Data
public class StackerTaskReq {

    /**
     * Id
     */
    private Long id;


    /**
     * 出入库单据号   堆垛机任务号 可以忽略
     */
    private String billNumber;


    /**
     * 建立日期
     */
    private String buildTime;


    /**
     * 单内序号  查询任务单号  可以忽略
     */
    private String detailId;

    /**
     * 任务开始时间 可以忽略
     */
    private String doBeginTime;

    /**
     * 任务结束时间 可以忽略
     */
    private String doEndTime;

    /**
     * 取货排
     */
    private String downZ;

    /**
     * 取货层
     */
    private String downY;

    /**
     * 取货列
     */
    private String downX;

    /**
     * 是否手动(0:手动 1.自动) 可以忽略
     */
    private String isHand;

    /**
     * 储位编码 123
     */
    private String locCode;


    /**
     * 设备编号(1,2,3,4,5…)
     */
    private Integer machineNum;

    /**
     * 任务状态(0:未开始  1:执行中  2.已完成  3.异常) 指定0
     */
    private String state;

    /**
     * 任务类型(1.入库  2.出库  3.移库  5.盘库  8.回库)
     */
    private String taskType;

    /**
     * 托盘号
     */
    private String trayCode;


    /**
     * 卸货排
     */
    private String upZ;


    /**
     * 卸货层
     */
    private String upY;


    /**
     * 卸货列
     */
    private String upX;

}
