package com.ruoyi.wms.basics.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 首页类基本货物信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
public class ViewPageGoodsDetailVo {


    /**
     * 库位编码
     */
    @Excel(name = "库位编码")
    private String locationCode;
    /**
     * 库位排序值
     */
    private int orderNo;

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
     * 机件号
     */
    private String partsCode;

    /**
     * 唯一码
     */
    private String onlyCode;

    /**
     * 生产日期
     */
    private String produceTime;


}
