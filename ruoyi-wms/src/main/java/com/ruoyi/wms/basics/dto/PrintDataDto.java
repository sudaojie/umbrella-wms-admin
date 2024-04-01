package com.ruoyi.wms.basics.dto;

import lombok.Data;

/**
 * 打印数据对象
 */
@Data
public class PrintDataDto {
    /**
     * 编码（托盘、机件号）
     */
    private String code;
    /**
     * 名称（托盘、机件号）
     */
    private String name;
    /**
     * 长度
     */
    private String length;
    /**
     * 宽度
     */
    private String width;
    /**
     * 高度
     */
    private String height;
    /**
     * 限重
     */
    private String weight;

    /**
     * 二维码地址url
     */
    private String url;
}
