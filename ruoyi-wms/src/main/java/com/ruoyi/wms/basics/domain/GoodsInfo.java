package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 货物信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_goods_info")
public class GoodsInfo extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 货物编码
     */
    @Excel(name = "货物编码")
    private String goodsCode;

    /**
     * 货物名称
     */
    @Excel(name = "货物名称")
    private String goodsName;

    /**
     * 货物简称
     */
    @Excel(name = "货物简称")
    private String goodsSimpleName;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

    /**
     * 所属库区
     */
//    @Excel(name = "所属库区")
    private String areaId;
    /**
     * 所属仓库
     */
//    @Excel(name = "所属仓库")
    private String warehouseId;

    /**
     * 货物类别
     */
//    @Excel(name = "货物类别")
    private String goodsCategoryId;

    /**
     * 计量单位
     */
    @Excel(name = "计量单位", readConverterExp = "1=盒,2=箱,3=套")
    private String measureUnit;

    /**
     * 包装方式
     */
    @Excel(name = "包装方式")
    private String packing;


    /**
     * 长(cm)
     */
    @Excel(name = "长(cm)")
    private BigDecimal length;

    /**
     * 宽(cm)
     */
    @Excel(name = "宽(cm)")
    private BigDecimal width;

    /**
     * 高(cm)
     */
    @Excel(name = "高(cm)")
    private BigDecimal height;
    /**
     * 高(cm)
     */
    @Excel(name = "堆放数量(个)")
    private Long num;

    /**
     * 体积（m³）
     */
    @Excel(name = "体积（m³）")
    private BigDecimal volume;

    /**
     * 重量(kg)
     */
    @Excel(name = "重量(kg)")
    private BigDecimal weight;

    /**
     * 最高库存
     */
    @Excel(name = "最高库存")
    private BigDecimal inventoryCountMax;

    /**
     * 最低库存
     */
    @Excel(name = "最低库存")
    private BigDecimal inventoryCountMin;

    /**
     * 质保期
     */
    @Excel(name = "质保期")
    private Integer warranty;

    /**
     * 启用状态;(enable:启用  disabled:禁用)
     */
//    @Excel(name = "启用状态;(0:启用  1:禁用)")
    private String enableStatus;

    /**
     * 入库-新建-货物选框筛选条件
     */
    @TableField(exist = false)
    private String rkHwxk;

}
