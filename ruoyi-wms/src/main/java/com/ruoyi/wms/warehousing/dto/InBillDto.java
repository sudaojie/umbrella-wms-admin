package com.ruoyi.wms.warehousing.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 待上架查询对象
 */
@Data
public class InBillDto {

    /**
     * 入库单号
     */
    private String inBillCode;
    /**
     * 入库数量
     */
    private BigDecimal inBillNum;
    /**
     * 已上架数量
     */
    private long listingNum;
    /**
     * 未上架数量
     */
    private long notListingNum;


}
