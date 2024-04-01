package com.ruoyi.wms.statistics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 出库单信息对象
 *
 * @author ruoyi
 * @date 2023-02-18
 */
@Data
@Accessors(chain = true)
@TableName("wms_out_bill")
public class OutBillStatistic extends BaseEntity{


    /** 编号 */
    private String id;

    /** 出库单号 */
    @Excel(name = "出库单号")
    private String outBillCode;

    /** 出库状态;(1.待拣货  2.拣货中  3.已出库 4.已作废) */
    @Excel(name = "出库状态;(1.待拣货  2.拣货中  3.已出库 4.已作废)")
    private String outBillStatus;

    /** 出库类别;(1.正常出库 2.调拨出库  3.报损出库  4.盘亏出库) */
    @Excel(name = "出库类别;(1.正常出库 2.调拨出库  3.报损出库  4.盘亏出库)")
    private String outBillCategory;

    /** 运货车牌号 */
    @Excel(name = "运货车牌号")
    private String freightVehicleNo;

    /** 收货地址 */
    @Excel(name = "收货地址")
    private String receiveAddress;

    /** 发付单位 */
    @Excel(name = "发付单位")
    private String issuingUnit;

    /** 运输方式 */
    @Excel(name = "运输方式")
    private String shippingType;

    /** 接收单位 */
    @Excel(name = "接收单位")
    private String receivingUnit;

    /** 发付依据 */
    @Excel(name = "发付依据")
    private String issuingBasis;

    /** 到站 */
    @Excel(name = "到站")
    private String address;

    /** 出库文号 */
    @Excel(name = "出库文号")
    private String outBillNo;

    /** 批次 */
    @TableField(exist = false)
    private String charg;

    /** 供应商编码 */
    @TableField(exist = false)
    private String supplierCode;

    /** 供应商名称 */
    @TableField(exist = false)
    private String supplierName;

    /** 货物编码 */
    @TableField(exist = false)
    private String goodsCode;

    /** 货物名称 */
    @TableField(exist = false)
    private String goodsName;

    /** 规格型号 */
    @TableField(exist = false)
    private String model;

    /** 计量单位 */
    @TableField(exist = false)
    private String measureUnit;

    /** 出库数量 */
    @TableField(exist = false)
    private BigDecimal outBillNum;

    /** 重量 */
    @TableField(exist = false)
    private BigDecimal weight;

    /** 体积 */
    @TableField(exist = false)
    private BigDecimal volume;
}
