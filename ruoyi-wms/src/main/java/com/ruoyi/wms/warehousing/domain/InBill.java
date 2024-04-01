package com.ruoyi.wms.warehousing.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 入库单信息对象
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_in_bill")
public class InBill extends BaseEntity {


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
     * 批次
     */
    @Excel(name = "批次")
    private String charg;

    /**
     * 入库单状态;(1.待收货  2.验货中  3.上架中   4.已上架  5.已作废)
     */
    @Excel(name = "入库单状态;(1.待收货  2.验货中  3.上架中   4.已上架  5.已作废)")
    private String inBillStatus;

    /**
     * 取盘状态（0-未取盘；1-已取盘；2取盘中）
     */
    private String takeTrayStatus;

    /**
     * 入库类别;(1.期初入库 2.普通入库  3.盘盈入库 4.晾晒入库  5.其他入库)
     */
    @Excel(name = "入库类别",dictType = "wms_in_stock_category")
    private String inBillCategory;

    /**
     * 入库数量
     */
    private BigDecimal inBillNum;

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
     * 根据文号
     */
    @Excel(name = "根据文号")
    private String docNo;

    /**
     * 入库流水号
     */
    @Excel(name = "入库流水号")
    private String inBillSerial;

    /**
     * 库房流水号
     */
    @Excel(name = "库房流水号")
    private String storageSerial;



    /**
     * 入库单详情信息信息
     */

    @TableField(exist = false)
    private List<InbillDetail> inbillDetailList;
    /**
     * 入库单详情具体货物信息信息
     */

    @TableField(exist = false)
    private List<InbillGoods> inbillGoodsDetailList;

    /**
     * 打印二维码
     */
    @TableField(exist = false)
    private String printCodeList;

    /**
     * 打印二维码名称
     */
    @TableField(exist = false)
    private String imgCode;

    public InBill() {
    }

    public InBill(String id, String inBillCode, String charg, String inBillStatus,String inBillCategory, String delflag,String createBy) {
        this.id = id;
        this.inBillCode = inBillCode;
        this.charg = charg;
        this.inBillStatus = inBillStatus;
        this.inBillCategory = inBillCategory;
        this.setDelFlag(delflag);
        this.setCreateBy(createBy);
        this.setCreateTime(new Date());
    }
}
