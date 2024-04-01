package com.ruoyi.wms.move.vo;

import lombok.Data;

@Data
public class WmsMoveDetailVo {
    /**
     * 移库详情主键
     */
    private String id;
    /**
     * 起始库区编码
     */
    private String startAreaCode;
    /**
     * 移库单号
     */
    private String moveCode;
}
