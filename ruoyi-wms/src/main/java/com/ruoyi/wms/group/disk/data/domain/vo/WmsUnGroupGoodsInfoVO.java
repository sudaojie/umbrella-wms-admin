package com.ruoyi.wms.group.disk.data.domain.vo;

import lombok.Data;

/**
 * @author hewei
 * @date 2023/4/19 0019 16:11
 * @description 未组盘数据货物Vo
 */
@Data
public class WmsUnGroupGoodsInfoVO {

    /**
     * 入库单号
     */
    private String inBillCode;

    /**
     * 机件号
     */
    private String partsCode;

    /**
     * 入库时间
     */
    private String createTime;

    /**
     * 货物类型
     */
    private String goodsType;

}
