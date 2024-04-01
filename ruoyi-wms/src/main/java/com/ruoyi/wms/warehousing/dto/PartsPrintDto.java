package com.ruoyi.wms.warehousing.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 机件号打印预览对象
 */
@Data
public class PartsPrintDto {
    /**
     * 主键
     */
    private String id;
    /**
     * 机件号
     */
    private String code;
    /**
     * 货物编码
     */
    private String goodsCode;
    /**
     * 货物名称
     */
    private String goodsName;
    /**
     * 批次
     */
    private String charg;
    /**
     * 型号
     */
    private String model;
    /**
     * 计量单位
     */
    private String jldw;
    /**
     * 供应商
     */
    private String gys;
    /**
     * 图片url
     */
    private String url;

    /**
     * 生成日期
     */
    private String produceTime;

    /**
     * 封存截止日期
     */
    private String periodValidity;

    /**
     * 封存保管期
     */
    private String  storageDate;

    /**
     * 机件号
     */
    private String partsCode;

    /**
     * 货物唯一码
     */
    private String onlyCode;
}
