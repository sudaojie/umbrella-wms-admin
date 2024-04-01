package com.ruoyi.wms.stock.dto;

import lombok.Data;

import java.util.List;

/**
 * 晾晒入库上架dto
 */
@Data
public class DryInbillPutOnDto {
    /**
     * 晾晒入库单编号
     */
    private String dryInbillCode;
    /**
     * 结束库区编码（agv禁用必传）
     */
    private String endAreaCode;

    /**
     * 晾晒托盘编码集合
     */
    private List<String> trayCodeList;

}
