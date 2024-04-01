package com.ruoyi.wms.warehousing.vo;

import lombok.Data;

/**
 * 唯一码vo
 */
@Data
public class OnlyCodeVo {
    /**
     * 入库单号
     */
    private String inBillCode;

    /**
     * 托盘堆放数量
     */
    private Long num;
    /**
     * 货物编码
     */
    private String goodsCode;

    /**
     * 唯一码
     */
    private String onlyCode;
}
