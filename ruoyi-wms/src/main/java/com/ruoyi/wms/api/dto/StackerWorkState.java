package com.ruoyi.wms.api.dto;

import lombok.Data;

/**
 * 查询输送线工作状态
 * @author select
 */
@Data
public class StackerWorkState {

    /**
     * 输送线
     */
    private String tfNum;

    /**
     * 输送线描述
     */
    private String text;

    /**
     * 输送线状态
     */
    private String status;
}
