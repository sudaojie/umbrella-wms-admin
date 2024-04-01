package com.ruoyi.wms.warehousing.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 机件号记录对象
 *
 * @author nf
 * @date 2023-02-14
 */
@Data
@Accessors(chain = true)
@TableName("wms_inbill_goods")
public class Parts extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 入库单号
     */
    @Excel(name = "入库单号",width = 25)
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
     * 规格型号
     */
    @Excel(name = "规格型号",width = 25)
    private String model;

    /**
     * 货物唯一码
     */
    @Excel(name = "货物唯一码",width = 25)
    private String onlyCode;

    /**
     * 机件号
     */
    @Excel(name = "机件号")
    private String partsCode;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期（yyyy-MM-dd）", width = 30, dateFormat = "yyyy-MM-dd")
    private String produceTime;

    /**
     * 出库状态(0.未出库  1.已出库)
     */
    private String outStatus;

    /**
     * 有效期
     */
    @TableField(exist = false)
    private String periodValidity;

    /**
     * 质保期
     */
    @TableField(exist = false)
    private String warranty;

    /**
     * 打印状态
     */
    @TableField(exist = false)
    private String printStatus;


    @TableField(exist = false)
    private String type;

    /**
     * 物品类型
     */
    @TableField(exist = false)
    private String categoryCode;

    /**
     * 类型名称
     */
    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String produceDate;

    @TableField(exist = false)
    private String periodValidityDate;

    @TableField(exist = false)
    private String[] goodsCodeList;

    @TableField(exist = false)
    private String charg;

    @TableField(exist = false)
    private String jldw;

    @TableField(exist = false)
    private String gys;

    @TableField(exist = false)
    private String partCodes;

}
