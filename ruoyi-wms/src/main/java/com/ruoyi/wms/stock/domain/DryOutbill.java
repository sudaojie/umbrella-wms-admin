package com.ruoyi.wms.stock.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 晾晒出库单对象
 *
 * @author ruoyi
 * @date 2023-03-03
 */
@Data
@Accessors(chain = true)
@TableName("wms_dry_outbill")
public class DryOutbill extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 晾晒出库单号 */
    @Excel(name = "晾晒出库单号")
    private String dryOutbillCode;

    /** 晾晒出库状态;(1、待出库 2、出库中 3、已出库) */
    @Excel(name = "晾晒出库类型;(1、待出库 2、出库中 3、已出库)")
    private String dryOutbillStatus;

    /** 出库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dryOutbillTime;

    /**
     * 晾晒出库类别;( 4.晾晒人库 )
     */
    @Excel(name = "晾晒出库类别;(4.晾晒入库)")
    private String dryOutBillCategory;

    /**
     * 晾晒出库单详情信息信息
     */

    @TableField(exist = false)
    private List<DryOutbillGoods> wmsDryOutbillGoodsList;

    /**
     * 库区编号
     */
    @TableField(exist = false)
    private String areaCode;

    /**
     * 锁定状态
     */
    @TableField(exist = false)
    private String lockStatus;

}
