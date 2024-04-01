package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 库存数量信息vo
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class TblstockCountVo {

    /**
     * 今日入库量 今日完成的入库单验收数量
     */
    private BigDecimal totdayInCount;

    /**
     * 今日出库量  今日完成的出库单数量
     */
    private BigDecimal totdayOutCount;

    /**
     * 库房库存数
     */
    private BigDecimal inTotalCount;

}
