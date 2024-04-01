package com.ruoyi.wms.outbound.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 出库单货物对象
 *
 * @author ruoyi
 * @date 2023-02-07
 */
@Data
@Accessors(chain = true)
@TableName("wms_outbill_goods")
public class OutbillGoods extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 库位编码
     */
    @Excel(name = "库位编码")
    private String locationCode;
    /**
     * 出库单号
     */
    @Excel(name = "出库单号")
    private String outBillCode;

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
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;

    /**
     * 出库数量
     */
    @Excel(name = "出库数量")
    private BigDecimal outBillNum;
    /**
     * 已拣货数量
     */
    @TableField(exist = false)
    private BigDecimal num;

    /**
     * 所属库区编码
     */
    @TableField(exist = false)
    private String areaCode;

    /**
     * 出库状态;(0-待拣货 1-已取出 2-已拣货)
     */
    @Excel(name = "出库状态;(0-待拣货 1-已取出 2-已拣货)")
    private String outBillStatus;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出库时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outBillTime;

    public OutbillGoods() {
    }

    public OutbillGoods(String id, String outBillCode, String onlyCode, String partsCode, String goodsCode, String goodsName, String model,
                        String measureUnit, String charg, String supplierCode, String supplierName, String trayCode, BigDecimal outBillNum,
                        String outBillStatus, Date outBillTime) {
        this.id = id;
        this.outBillCode = outBillCode;
        this.onlyCode = onlyCode;
        this.partsCode = partsCode;
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.model = model;
        this.measureUnit = measureUnit;
        this.charg = charg;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.trayCode = trayCode;
        this.outBillNum = outBillNum;
        this.outBillStatus = outBillStatus;
        this.outBillTime = outBillTime;
        this.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        this.setCreateBy(SecurityUtils.getUsername());
        this.setCreateTime(new Date());
    }
}
