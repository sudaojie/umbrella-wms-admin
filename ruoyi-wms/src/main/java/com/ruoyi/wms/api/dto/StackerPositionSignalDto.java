package com.ruoyi.wms.api.dto;

import lombok.Data;

/**
 * 堆垛机到位信息号Dto
 */
@Data
public class StackerPositionSignalDto {

    /**
     * 堆垛机编号
     */
    private String stackerId;


    /**
     * 托盘号
     */
    private String trayCode;


    /**
     * 堆垛机到位类型(1.入库货物到位  2.出库货物传输带到位)
     */
    private String stackerSignalType;


    /**
     * 任务号
     */
    private String taskNo;


}
