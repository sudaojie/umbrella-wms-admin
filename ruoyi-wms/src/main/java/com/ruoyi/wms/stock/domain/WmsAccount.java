package com.ruoyi.wms.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存台账对象
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_account")
public class WmsAccount extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 单据编号 */
    @Excel(name = "单据编号")
    private String accountCode;

    /** 单据类型(0.入库单 1.出库单 2.无单上架 3.无单下架) */
    @Excel(name = "单据类型(0.入库单 1.出库单 2.无单上架 3.无单下架)")
    private String codeType;

    /** 批次号 */
    @Excel(name = "批次号")
    private String charg;

    /** 仓库编号 */
    @Excel(name = "仓库编号")
    private String warehouseCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String warehouseName;

    /** 变动数量 */
    @Excel(name = "变动数量")
    private String changeNum;

    /** 结存量 */
    @Excel(name = "结存量")
    private String stockNum;

    /** 货物编码 */
    @Excel(name = "货物编码")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 规格型号 */
    @Excel(name = "规格型号")
    private String model;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;



}
