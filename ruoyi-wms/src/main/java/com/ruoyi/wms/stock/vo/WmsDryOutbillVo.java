package com.ruoyi.wms.stock.vo;

import lombok.Data;

import java.util.List;

/**
 * 晾晒出库离线数据vo
 */
@Data
public class WmsDryOutbillVo {
    /**
     * 晾晒出库单号
     */
    private String dryOutbillCode;

    /**
     * 托盘号列表
     */
    private List<WmsDryOutbillGoodsVo> trayList;

}
