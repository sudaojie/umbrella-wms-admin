package com.ruoyi.wms.group.disk.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author hewei
 * @date 2023/4/19 0019 16:11
 * @description 未组盘数据货物Vo
 */
@Data
public class WmsUnGroupVO {

    /**
     * 入库单号
     */
    private String inBillCode;

    /**
     * 未组盘数据列表
     */
    private List<WmsUnGroupGoodsInfoVO> list;

}
