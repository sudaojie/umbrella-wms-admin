package com.ruoyi.wms.gendata.dto;

import lombok.Data;

/**
 * @author Administrator
 * @create 2023-06-20 9:12
 */
@Data
public class GenDto {

    /**
     * 入库单号
     */
    private String inBillCode;

    /**
     * 结束库区
     */
    private String endAreaCode;
}
