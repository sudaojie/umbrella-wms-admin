package com.ruoyi.wms.warehousing.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 上架单对象
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@Data
@Accessors(chain = true)
@TableName("wms_listing_list")
public class ListingList extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 上架单号
     */
    @Excel(name = "上架单号")
    private String listingCode;

    /**
     * 入库单号
     */
    @Excel(name = "入库单号")
    private String inBillCode;

    /**
     * 批次
     */
    @Excel(name = "批次")
    private String charg;

}
