package com.ruoyi.wms.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 晾晒出库单货物对象 dry_outbill_goods
 *
 * @author ruoyi
 * @date 2023-03-03
 */
@Data
@Accessors(chain = true)
@TableName("wms_dry_outbill_goods")
public class DryOutbillGoods extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 晾晒出库单号
     */
    @Excel(name = "晾晒出库单号")
    private String dryOutbillCode;

    /**
     * 机件号
     */
    @Excel(name = "机件号")
    private String partsCode;

    /**
     * 批次号
     */
    @Excel(name = "批次号")
    private String charg;

    /**
     * 出库时间
     */
    @Excel(name = "出库时间")
    private String dryOutbillTime;

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
     * 晾晒出库状态;(0、待出库 1、出库中 2、已出库)
     */
    @Excel(name = "晾晒出库类型;(0、待出库 1、出库中 2、已出库)")
    private String dryOutbillStatus;

    /**
     * 计量单位
     */
    @Excel(name = "计量单位")
    private String measureUnit;

    /**
     * 单位规格
     */
    @Excel(name = "规格")
    private String model;

    /**
     * 出库数量
     */
    @Excel(name = "出库数量")
    private String goodsNum;

    /**
     * 仓库编号
     */
    @Excel(name = "仓库编号")
    private String warehouseCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    private String warehouseName;

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
     * 是否创建入库单（0未创建，1已创建）
     */
    private String lockStatus;

}
