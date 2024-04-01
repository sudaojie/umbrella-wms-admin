package com.ruoyi.wms.warehousing.dto;

import com.ruoyi.wms.warehousing.domain.InbillGoods;
import lombok.Data;

import java.util.List;

@Data
public class InbillGoodsDto {
    //入库单号
    String inBillCode;

    //入库详情id
    String inBillDetailId;

    //机件号集合
    List<InbillGoods> inBillGoods;
}
