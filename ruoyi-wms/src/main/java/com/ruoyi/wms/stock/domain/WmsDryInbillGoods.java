package com.ruoyi.wms.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 晾晒出入库单货物对象 wms_dry_outbill_goods
 *
 * @author nf
 * @date 2023-03-10
 */

/**
 * 晾晒入库单货物对象
 *
 * @author ruoyi
 * @date 2023-03-12
 */
@Data
@Accessors(chain = true)
@TableName("wms_dry_inbill_goods")
public class WmsDryInbillGoods extends BaseEntity {


    /**
     * 主键ID
     */
    private String id;

    /**
     * 晾晒入库单号
     */
    @Excel(name = "晾晒入库单号")
    private String dryInbillCode;

    /**
     * 晾晒入库入库状态(0、待入库 1、组盘中 2、已组盘 3.入库中 4.已入库)
     */
    @Excel(name = "晾晒入库入库状态")
    private String dryInbillStatus;

    /**
     * 批次号
     */
    @Excel(name = "批次号")
    private String charg;

    /**
     * 机件号
     */
    @Excel(name = "机件号")
    private String partsCode;

    /**
     * 货物编号
     */
    @Excel(name = "货物编号")
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
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

    /**
     * 入库数量
     */
    @Excel(name = "入库数量")
    private String goodsNum;

    /**
     * 库区编号
     */
    @Excel(name = "库区编号")
    private String areaCode;

    /**
     * 库区名称
     */
    @Excel(name = "库区名称")
    private String areaName;

    /**
     * 库位编号
     */
    @Excel(name = "库位编号")
    private String locationCode;

    /**
     * 库位名称
     */
    @Excel(name = "库位名称")
    private String locationName;

    /**
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入库时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dryInbillTime;


}
