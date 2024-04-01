package com.ruoyi.wms.api.dto;

import lombok.Data;

/**
 * AGV到位信号Dto
 */
@Data
public class AgvPositionSignalDto {

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * AGV到位类型(1.入库上架至传输带到位  2.出库下架至规划库位到位)
     */
    private String agvSignalType;

}
