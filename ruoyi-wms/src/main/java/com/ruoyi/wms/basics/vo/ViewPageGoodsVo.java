package com.ruoyi.wms.basics.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页类基本货物信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class ViewPageGoodsVo {


    /**
     * 库位编码
     */
    @Excel(name = "库位编码")
    private String locationCode;

    /**
     * 库位名称
     */
    @Excel(name = "库位名称")
    private String locationName;

    /**
     * 当前库位上的托盘编码
     */
    @Excel(name = "当前库位上的托盘编码")
    private String trayCode;

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
     * 规格型号
     */
    @Excel(name = "规格型号")
    private String model;

    /**
     * 计量单位
     */
    @Excel(name = "计量单位", readConverterExp = "1=盒,2=箱,3=套")
    private String measureUnit;

    /**
     * 堆放数量
     */
    @Excel(name = "堆放数量(个)")
    private int num;

    /**
     * 排序值
     */
    private int orderNo;

    /**
     * 库位状态
     */
    private String locationStatus;

    /**
     * 库位类型(1.母库位 2.子库位)
     */
    private String locationType;

    /**
     * 启用状态(0:启用 1:禁用)
     */
    private String enableStatus;

    /**
     * 货物详情
     */
    private List<GoodsDetailVo> goodsDetailVoList;

}
