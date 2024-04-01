package com.ruoyi.wms.stock.vo;

import lombok.Data;

import java.util.List;

/**
 * 晾晒入库上架页面查看详情vo
 */
@Data
public class DryInBillTrayVo {
    /**
     * 托盘编码
     */
    private String trayCode;

    /**
     * 组盘状态(true:已组盘  false:未组盘)
     */
    private boolean takeStatus;


    /**
     * 机件号列表
     */
    private List<String> partsCodeList;

}
