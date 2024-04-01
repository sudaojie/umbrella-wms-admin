package com.ruoyi.wms.outbound.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 出库单信息对象
 *
 * @author nf
 * @date 2023-02-18
 */
@Data
@Accessors(chain = true)
@TableName("wms_out_bill")
public class OutBill extends BaseEntity{


    /** 编号 */
    private String id;

    /** 出库单号 */
    @Excel(name = "出库单号")
    private String outBillCode;

    /** 出库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出库时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outBillTime;

    /** 出库状态 */
    @Excel(name = "出库状态")
    private String outBillStatus;

    /** 出库类别 */
    @Excel(name = "出库类别")
    private String outBillCategory;

    /** 运货车牌号 */
    @Excel(name = "运货车牌号")
    private String freightVehicleNo;

    /** 收货地址 */
    @Excel(name = "收货地址")
    private String receiveAddress;

    /** 发付单位 */
//    @Excel(name = "发付单位")
    private String issuingUnit;

    /** 运输方式 */
//    @Excel(name = "运输方式")
    private String shippingType;

    /** 接收单位 */
//    @Excel(name = "接收单位")
    private String receivingUnit;

    /** 发付依据 */
//    @Excel(name = "发付依据")
    private String issuingBasis;

    /** 到站 */
//    @Excel(name = "到站")
    private String address;

    /** 出库文号 */
//    @Excel(name = "出库文号")
    private String outBillNo;


    /** 二维码图片名称 */
    @TableField(exist = false)
    private String imgCode;
    /** 二维码网络地址 */
    @TableField(exist = false)
    private String url;

    @TableField(exist = false)
    private List<OutbillGoods> outbillGoodsList;

    public OutBill() {
    }

    public OutBill(String id, String outBillCode, Date outBillTime, String outBillStatus, String outBillCategory) {
        this.id = id;
        this.outBillCode = outBillCode;
        this.outBillTime = outBillTime;
        this.outBillStatus = outBillStatus;
        this.outBillCategory = outBillCategory;
        this.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        this.setCreateBy(SecurityUtils.getUsername());
        this.setCreateTime(new Date());
    }
}
