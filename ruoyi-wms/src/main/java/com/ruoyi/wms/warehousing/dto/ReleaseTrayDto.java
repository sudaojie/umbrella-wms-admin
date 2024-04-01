package com.ruoyi.wms.warehousing.dto;

import lombok.Data;

import java.util.List;

/**
 * 解盘dto
 */
@Data
public class ReleaseTrayDto {
    /**
     * 托盘编码list
     */
    private List<String> trayCodeList;

    /**
     * 入库单号
     */
    private String inBillCode;
}
