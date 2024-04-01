package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 首页类货物详情信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class GoodsDetailVo {


    /**
     * 机件号
     */
    private String partCode;

    /**
     * 唯一码
     */
    private String onlyCode;

    /**
     * 生产日期
     */
    private String produceTime;
}
