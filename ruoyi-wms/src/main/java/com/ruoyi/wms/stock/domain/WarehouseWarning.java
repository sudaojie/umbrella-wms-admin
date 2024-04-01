package com.ruoyi.wms.stock.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存总览对象
 *
 * @author ruoyi
 * @date 2023-03-10
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_tblstock")
public class WarehouseWarning extends BaseEntity{


    /** 主键 */
    private String id;

    /** 货物唯一码 */
    @Excel(name = "货物唯一码")
    private String onlyCode;

    /** 机件号 */
    @Excel(name = "机件号")
    private String partsCode;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 库存数量 */
    @TableField(exist = false)
    private BigDecimal goodsNum;

    /** 可用数量 */
    @TableField(exist = false)
    private String availableNum;

    /** 缺货数量 */
    @TableField(exist = false)
    private int lackNum;

    /** 超储数量 */
    @TableField(exist = false)
    private int overNum;

    /**
     * 最高库存
     */
    @Excel(name = "最高库存")
    private BigDecimal inventoryCountMax;

    /**
     * 最低库存
     */
    @Excel(name = "最低库存")
    private BigDecimal inventoryCountMin;

    /**
     * 滞压天数
     */
    @TableField(exist = false)
    private int listingDate;

    /**
     * 滞压月份
     */
    @TableField(exist = false)
    private int detainedMonth;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String model;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 批次 */
    @Excel(name = "批次")
    private String charg;

    /** 供应商编码 */
    @Excel(name = "供应商编码")
    private String supplierCode;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String supplierName;

    /** 仓库编号 */
    @Excel(name = "仓库编号")
    private String warehouseCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String warehouseName;

    /** 库区编号 */
    @Excel(name = "库区编号")
    private String areaCode;

    /** 库区名称 */
    @Excel(name = "库区名称")
    private String areaName;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上架时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date listingTime;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date produceTime;

    /** 质保期;天 */
    @Excel(name = "质保期;天")
    private Long warranty;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期")
    private Date periodValidity;

    /** 超时 */
    @TableField(exist = false)
    private int overTime;

    /** 状态 */
    @TableField(exist = false)
    private String status;

    /** 锁定状态（0-未锁定 1-已锁定） */
    @Excel(name = "锁定状态", readConverterExp = "0=-未锁定,1=-已锁定")
    private String lockStatus;

    /** 库存预警策略 */
    @Excel(name = "库存预警策略")
    private String warningProxy;


}
