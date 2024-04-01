package com.ruoyi.wms.warehousing.domain;

import cn.hutool.core.util.IdUtil;
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

/**
 * 入库单货物对象
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_inbill_goods")
public class InbillGoods extends BaseEntity {

    /**
     * 主键
     */
    private String id;

    ;
    /**
     * 入库单号
     */
    @Excel(name = "入库单号")
    private String inBillCode;

    ;
    /**
     * 入库详情主键
     */
    @Excel(name = "入库详情主键")
    private String inbillDetailId;
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
     * 库位编码
     */
    @Excel(name = "托盘编号")
    private String locationCode;
    /**
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;
    /**
     * 打印状态
     */
    private String printStatus;
    /**
     * 入库数量
     */
    @Excel(name = "入库数量")
    private BigDecimal inBillNum;
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
     * 出库状态(0.未出库  1.已出库)
     */
    private String outStatus;

    /**
     * 货物编码
     */
    private String goodCode;

    /**
     * 货物名称
     */
    @TableField(exist = false)
    private String goodsName;
    /**
     * 货物编码
     */
    @TableField(exist = false)
    private String goodsCode;
    /**
     * 计量单位
     */
    @TableField(exist = false)
    private String measureUnit;
    /**
     * 导入使用的生产日期
     */
    @TableField(exist = false)
    private String produceDate;

    @TableField(exist = false)
    private String inbillGoodsCode;

    public InbillGoods() {
    }

    public InbillGoods(String inBillCode, String inbillDetailId, String onlyCode, BigDecimal inBillNum,String goodCode) {
        this.inBillCode = inBillCode;
        this.inbillDetailId = inbillDetailId;
        this.onlyCode = onlyCode;
        this.inBillNum = inBillNum;
        this.goodCode = goodCode;
    }

    public InbillGoods(String id, String inBillCode, String inbillDetailId, String onlyCode, String partsCode, String locationCode, String trayCode,
                       String printStatus, BigDecimal inBillNum, Date produceTime, Date periodValidity) {
        this.id = id;
        this.inBillCode = inBillCode;
        this.inbillDetailId = inbillDetailId;
        this.onlyCode = onlyCode;
        this.partsCode = partsCode;
        this.locationCode = locationCode;
        this.trayCode = trayCode;
        this.printStatus = printStatus;
        this.inBillNum = inBillNum;
        this.produceTime = produceTime;
        this.periodValidity = periodValidity;
        this.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        this.setCreateBy(SecurityUtils.getUsername());
        this.setCreateTime(new Date());
    }
}
