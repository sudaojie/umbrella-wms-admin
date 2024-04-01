package com.ruoyi.wms.stock.vo;

import com.ruoyi.wms.stock.dto.PartsCode;
import lombok.Data;

import java.util.List;

/**
 * 晾晒出库托盘vo
 */
@Data
public class WmsDryOutbillGoodsVo {
    /**
     * 托盘编码
     */
    private String trayCode;
    /**
     * 状态（部分，全部）
     */
    private String status;
    /**
     * 机件号列表
     */
    private List<PartsCode> partsCodeList;

}
