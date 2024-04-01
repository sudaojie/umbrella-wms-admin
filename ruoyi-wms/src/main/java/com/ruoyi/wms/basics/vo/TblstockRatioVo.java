package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 库存周转率信息vo
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class TblstockRatioVo {

    /**
     * 总库位
     */
    private BigDecimal totalCount;

    /**
     * 已使用库位 有托盘有货、移库库位
     */
    private BigDecimal inUseCount;

    /**
     * 空闲库位
     */
    private BigDecimal spareCount;

    /**
     * 本月期初库存
     */
    private String openingInventoryOfThisMonth;

    /**
     * 本月期末库存
     */
    private String endingInventoryOfThisMonth;

    /**
     * 本月出库数量
     */
    private String outGoodsNumOfThisMonth;

    /**
     * 上月期初库存
     */
    private String openingInventoryOfLastMonth;

    /**
     * 上月期末库存
     */
    private String endingInventoryOfLastMonth;

    /**
     * 上月出库数量
     */
    private String outGoodsNumOfLastMonth;

}
