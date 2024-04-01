package com.ruoyi.wcs.req.stacker;

import lombok.Data;

/**
 * 获取堆垛机状态响应类
 */
@Data
public class StackerSrmStatusRsp {

    /**
     * 任务类型（1.入库  2.出库  3.移库）
     */
    private Integer taskType;

    /**
     * 堆垛机编号
     */
    private Integer stackerId;

    /**
     * 任务号
     */
    private Integer taskNo;

    /**
     * 联机状态(0.未联机 1.联机)
     */
    private Integer onlineStatus;

    /**
     * 输送机联机状态(0.手动 1.自动  2.故障)
     */
    private Integer transferStatus;

    /**
     * 任务状态(0.无任务  1.执行中  2.完成)
     */
    private Integer status;
}
