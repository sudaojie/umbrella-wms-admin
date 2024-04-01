package com.ruoyi.wms.statistics.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 入库单信息对象
 *
 * @author ruoyi
 * @date 2023-02-17
 */
@Data
@Accessors(chain = true)
@TableName("wms_in_bill")
public class InBillStatistic extends BaseEntity{


    /** 主键 */
    private String id;

    /** 入库单号 */
    @Excel(name = "入库单号")
    private String inBillCode;

    /** 批次 */
    @Excel(name = "批次")
    private String charg;

    /** 入库单状态;(1.待收货  2.验货中  3.上架中   4.已上架  5.已作废) */
    @Excel(name = "入库单状态;(1.待收货  2.验货中  3.上架中   4.已上架  5.已作废)")
    private String inBillStatus;

    /** 入库类别;(1.期初入库 2.普通入库  3.盘盈入库 4.晾晒入库  5.其他入库) */
    @Excel(name = "入库类别;(1.期初入库 2.普通入库  3.盘盈入库 4.晾晒入库  5.其他入库)")
    private String inBillCategory;

    /** 重量(kg) */
    @Excel(name = "重量(kg)")
    private BigDecimal weight;

    /** 体积(m³) */
    @Excel(name = "体积(m³)")
    private BigDecimal volume;

    /** 根据文号 */
    @Excel(name = "根据文号")
    private String docNo;

    /**
     * 货物编码
     */
    @TableField(exist = false)
    private String goodsCode;

    /**
     * 货物名称
     */
    @TableField(exist = false)
    private String goodsName;

    /**
     * 计量单位
     */
    @TableField(exist = false)
    private String measureUnit;

    /**
     * 规格
     */
    @TableField(exist = false)
    private String model;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    private String supplierCode;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    private String supplierName;

    /**
     * 入库数量
     */
    @TableField(exist = false)
    private BigDecimal inBillNum;

}
