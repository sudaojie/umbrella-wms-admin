package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 库区选项
 */
@Data
@Accessors(chain = true)
public class SelectedVo {

    /**
     * 选择项value
     */
    private String value;
    /**
     * 选择项label
     */
    private String label;
}
