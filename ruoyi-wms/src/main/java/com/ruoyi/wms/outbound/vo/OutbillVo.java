package com.ruoyi.wms.outbound.vo;

import lombok.Data;

import java.util.List;

/**
 * 出库离线数据
 */
@Data
public class OutbillVo {
    //托盘编码
    private String trayCode;
    //出库编码
    private String outBillCode;
    //状态（部分，全部）
    private String status;
    //机件号列表
    private List<PartsCodeVo> partsCodeList;

}
