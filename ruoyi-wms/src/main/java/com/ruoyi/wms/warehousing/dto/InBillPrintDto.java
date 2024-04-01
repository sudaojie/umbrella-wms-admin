package com.ruoyi.wms.warehousing.dto;

import com.ruoyi.wms.warehousing.domain.InbillDetail;
import lombok.Data;

import java.util.List;

/**
 * 入库单打印
 */
@Data
public class InBillPrintDto {
    /**
     * 入库单号
     */
    private String inBillCode;
    /**
     * 入库类别
     */
    private String inBillCategory;
    /**
     * 制单人
     */
    private String createBy;
    /**
     * 制单时间
     */
    private String createTime;

    /**
     * 图片url
     */
    private String printCodeList;

    private List<InbillDetail> inbillDetailList;
}
