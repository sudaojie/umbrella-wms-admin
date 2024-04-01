package com.ruoyi.wms.warehousing.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 上架单详情对象
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@Data
@Accessors(chain = true)
@TableName("wms_listing_detail")
public class ListingDetail extends BaseEntity {


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
    private String charg;

    /**
     * 货物唯一码
     */
    @Excel(name = "货物唯一码")
    private String onlyCode;

    /**
     * 机件号
     */
    @Excel(name = "机件号")
    private String partsCode;

    /**
     * 货物编码
     */
    @Excel(name = "货物编码")
    private String goodsCode;

    /**
     * 货物名称
     */
    @Excel(name = "货物名称")
    private String goodsName;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

    /**
     * 计量单位
     */
    @Excel(name = "计量单位")
    private String measureUnit;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    private String supplierName;

    /**
     * 托盘id
     */
    private String trayId;
    /**
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;
    /**
     * 库位编号
     */
    private String locationCode;
    /**
     * 理货区库区编码
     */
    private String areaCode;

    /**
     * 上架状态(0-未上架 1-已上架 2-上架中 3-上架失败)
     */
    @Excel(name = "上架状态")
    private String listingStatus;

    /**
     * 上架时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上架时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date listingTime;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date produceTime;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date periodValidity;

    /**
     * 质保期;天
     */
    @Excel(name = "质保期(天)")
    private Long warranty;



}
