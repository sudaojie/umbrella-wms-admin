package com.ruoyi.wms.warehousing.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 入库单详情信息对象
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_inbill_detail")
public class InbillDetail extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 入库单号
     */
    @Excel(name = "入库单号")
    private String inBillCode;

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
     * 计量单位
     */
    @Excel(name = "计量单位")
    private String measureUnit;

    /**
     * 重量(kg)
     */
    @Excel(name = "重量(kg)")
    private BigDecimal weight;

    /**
     * 体积(m³)
     */
    @Excel(name = "体积(m³)")
    private BigDecimal volume;
    /**
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

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
     * 预报数量
     */
    @Excel(name = "预报数量")
    private BigDecimal reportNum;

    /**
     * 入库数量
     */
    @Excel(name = "入库数量")
    private BigDecimal inBillNum;

    /**
     * 质保期;天
     */
    @Excel(name = "质保期;天")
    private Long warranty;

    /**
     * 类别编码
     */
    private String categoryCode;

    /** 堆放数量(个) */
    private Long num;

    /** 已取托盘数量 */
    private Long takedTrayCount;

    /**
     * 推荐托盘数量
     */
    @TableField(exist = false)
    private Long trayCount;

    /**
     * 差异量
     */
    @TableField(exist = false)
    private BigDecimal variance;

    public InbillDetail() {
    }

    public InbillDetail(String id, String inBillCode, String goodsCode, String goodsName, String measureUnit, BigDecimal weight, BigDecimal volume, String model, String supplierCode,
                        String supplierName, Long warranty) {
        this.id = id;
        this.inBillCode = inBillCode;
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.measureUnit = measureUnit;
        this.weight = weight;
        this.volume = volume;
        this.model = model;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.warranty = warranty;
        this.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        this.setCreateBy(SecurityUtils.getUsername());
        this.setCreateTime(new Date());
    }
}
