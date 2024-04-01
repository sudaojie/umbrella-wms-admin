package com.ruoyi.wms.stock.dto;

import lombok.Data;

import java.util.List;

/**
 * 晾晒入库PDA查询
 */
@Data
public class WmsDryInbillGoodsVo {
    //托盘编码
    private String trayCode;
    //晾晒入库编码
    private String dryInbillCode;
    //状态（部分，全部）
    private String status;
    //机件号列表
    private List<PartsCode> partsCodeList;

}
