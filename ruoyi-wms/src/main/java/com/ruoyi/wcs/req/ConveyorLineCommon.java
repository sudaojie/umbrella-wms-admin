package com.ruoyi.wcs.req;

import lombok.Data;

/**
 * 输送线状态
 * @author select
 */
@Data
public class ConveyorLineCommon {

    /**
     * 输送线状态
     */
    private String state;

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * 输送线编号
     */
    private String conveyorNo;

    /**
     * 任务类型 （1.入库 2.出库）
     */
    private int taskType;
}
