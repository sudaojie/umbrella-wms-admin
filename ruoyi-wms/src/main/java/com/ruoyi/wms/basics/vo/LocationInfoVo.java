package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 库位信息vo
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class LocationInfoVo {

    /**
     * 总库位
     */
    private Long totalCount;

    /**
     * 已使用库位 有托盘有货、移库库位
     */
    private Long inUseCount;

    /**
     * 空闲库位
     */
    private Long spareCount;

}
