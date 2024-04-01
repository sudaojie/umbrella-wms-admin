package com.ruoyi.wms.nolist.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 无单上架对象
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@Data
@Accessors(chain = true)
@TableName("wms_listing_nolist")
public class ListingNolist extends BaseEntity{


    /** 主键 */
    private String id;

    /** 上架单号 */
    @Excel(name = "上架单号")
    private String listingCode;

    /** 货物唯一码 */
    @Excel(name = "货物唯一码")
    private String onlyCode;

    /** 机件号 */
    @Excel(name = "机件号")
    private String mpCode;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String model;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 批次 */
    @Excel(name = "批次")
    private String charg;

    /** 库位编号 */
    @Excel(name = "库位编号")
    private String locationCode;

    /** 库位名称 */
    @Excel(name = "库位名称")
    private String locationName;

    /** 托盘编号 */
    @Excel(name = "托盘编号")
    private String trayCode;

    /** 上架时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上架时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date listingTime;

    /** 上架状态;(0-未上架 1-已上架 2-上架中) */
    @Excel(name = "上架状态;(0-未上架 1-已上架 2-上架中)")
    private String listingStatus;


}
