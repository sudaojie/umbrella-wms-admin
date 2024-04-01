package com.ruoyi.wms.stock.domain;

import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.wms.stock.dto.WmsDryInbillGoodsVo;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 晾晒入库单对象
 *
 * @author nf
 * @date 2023-03-10
 */
@Data
@Accessors(chain = true)
@TableName("wms_dry_inbill")
public class WmsDryInbill extends BaseEntity {


    /**
     * 主键ID
     */
    private String id;

    /**
     * 晾晒出库单号
     */
    private String dryOutbillCode;

    /**
     * 晾晒入库单号
     */
    @Excel(name = "晾晒入库单号")
    private String dryInbillCode;

    /**
     * 晾晒入库状态(0、待入库 1、组盘中 2、已组盘 3.入库中 4.已入库)
     */
    @Excel(name = "晾晒入库状态")
    private String dryInbillStatus;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "入库时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dryInbillTime;


    /**
     * 晾晒出入库单货物信息
     */
    @TableField(exist = false)
    private List<WmsDryInbillGoods> wmsDryInbillGoodsList;

    /**
     * 晾晒出入库单托盘分组货物信息
     */
    @TableField(exist = false)
    private List<WmsDryInbillGoodsVo> wmsDryInbillGoodsVoList;

}
