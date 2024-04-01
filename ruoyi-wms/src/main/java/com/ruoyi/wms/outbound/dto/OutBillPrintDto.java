package com.ruoyi.wms.outbound.dto;

import com.ruoyi.wms.basics.vo.GoodsDetailVo;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import lombok.Data;

import java.util.List;

/**
 * 出库单打印对象
 */
@Data
public class OutBillPrintDto {
    /**
     * 出库单号
     */
    private String outBillCode;
    /**
     * 出库类别
     */
    private String outBillCategory;
    /**
     * 制单人
     */
    private String createBy;
    /**
     * 制单时间
     */
    private String createTime;
    /**
     * 图片路径
     */
    private String imgCode;
    /**
     * 图片url
     */
    private String url;

    private List<OutbillGoods> outbillGoodsList;

}
