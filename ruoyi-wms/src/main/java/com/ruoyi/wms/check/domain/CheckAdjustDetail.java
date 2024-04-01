package com.ruoyi.wms.check.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存盘点调整单详情对象
 *
 * @author nf
 * @date 2023-03-23
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check_adjust_detail")
public class CheckAdjustDetail extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 盘点单号 */
    private String checkBillCode;

    /** 盘点货物编码 */
    @Excel(name = "盘点货物编码")
    private String goodsCode;

    /** 盘点货物名称 */
    private String goodsName;

    /** 盘点库区编号 */
    @Excel(name = "盘点库区编号")
    private String areaCode;

    /** 盘点库区名称 */
    private String areaName;

    /** 盘点库位编号 */
    @Excel(name = "盘点库位编号")
    private String locationCode;

    /** 盘点库位名称 */
    private String locationName;

    /** 盘点托盘编号 */
    @Excel(name = "盘点托盘编号")
    private String trayCode;

    /** 账面数量 */
    @Excel(name = "账面数量")
    private String curtainNum;

    /** 盘点数量 */
    @Excel(name = "盘点数量")
    private String checkNum;

    /** 盘亏数量 */
    @Excel(name = "盘亏数量")
    private String lossNum;

    /** 盘盈数量 */
    @Excel(name = "盘盈数量")
    private String profitNum;



}
