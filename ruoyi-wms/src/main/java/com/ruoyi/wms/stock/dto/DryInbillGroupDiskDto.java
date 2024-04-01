package com.ruoyi.wms.stock.dto;

import lombok.Data;

/**
 * 晾晒入库组盘dto
 */
@Data
public class DryInbillGroupDiskDto {
    /**
     * 晾晒入库单编号
     */
    private String dryInbillCode;
    /**
     * 托盘编号
     */
    private String trayCode;

}
