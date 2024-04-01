package com.ruoyi.wms.outbound.dto;

import com.ruoyi.wms.basics.dto.TrayDto;
import lombok.Data;

import java.util.List;

@Data
public class OutbillGoodsDto {
    /**
     * 结束库区编码（理货区）
     */
    private String endAreaCode;
    /**
     * 托盘编码
     */
    private String trayCode;
    //出库单号
    private String outBillCode;
    //托盘编号
    private List<String> trayCodeList;

    /**
     * 机件号集合
     */
    private List<String> partsCodeList;

    /**
     * 托盘对象集合
     */
    private List<TrayDto> trayList;
}
