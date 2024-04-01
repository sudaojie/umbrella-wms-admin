package com.ruoyi.wms.group.disk.data.domain;

import lombok.Data;

/**
 * @author hewei
 * @date 2023/4/19 0019 13:22
 */
@Data
public class WmsGroupDiskGoodsInfo {

    /**
     * 入库单号
     */
    private String inBillCode;

    /**
     * 唯一码
     */
    private String onlyCode;

    /**
     * 机件号
     */
    private String partsCode;

    /**
     * 托盘编码
     */
    private String trayCode;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 货物编码
     */
    private String goodsCode;

    /**
     * 供应商名称
     */
    private String supplierName;

}
