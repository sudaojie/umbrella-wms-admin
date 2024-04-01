package com.ruoyi.wms.stock.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 库存总览对象
 *
 * @author ruoyi
 * @date 2023-02-06
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_tblstock")
public class Tblstock extends BaseEntity {


    /**
     * 主键
     */
    private String id;

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
     * 批次
     */
    @Excel(name = "批次")
    private String charg;

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
     * 仓库编号
     */
    @Excel(name = "仓库编号")
    private String warehouseCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    private String warehouseName;

    /**
     * 库区编号
     */
    @Excel(name = "库区编号")
    private String areaCode;

    /**
     * 库区名称
     */
    @Excel(name = "库区名称")
    private String areaName;

    /**
     * 库位编号
     */
    @Excel(name = "库位编号")
    private String locationCode;

    /**
     * 库位名称
     */
    @Excel(name = "库位名称")
    private String locationName;

    /**
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;

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
    @Excel(name = "质保期;天")
    private Long warranty;
    /**
     * 锁定状态
     */
    @Excel(name = "锁定状态")
    private String lockStatus;

    /**
     * 货物数量
     */
    @TableField(exist = false)
    private String goodsNum;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date inBillDate;
    /**
     * 上次晾晒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date lastDryDate;

}
