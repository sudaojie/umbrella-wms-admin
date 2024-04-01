package com.ruoyi.wms.stock.dto;

import lombok.Data;

/**
 * 查询参数
 */
@Data
public class TblstockDto {
    /**
     * 综合查询参数
     */
    private String param;
    /**
     * 托盘编码
     */
    private String trayCode;
}
