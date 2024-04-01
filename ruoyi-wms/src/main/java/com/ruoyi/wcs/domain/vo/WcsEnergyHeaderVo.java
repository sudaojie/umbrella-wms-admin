package com.ruoyi.wcs.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author hewei
 * @date 2023/4/11 0011 09:26
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WcsEnergyHeaderVo {

    /**
     * 时间段
     */
    private String time;

    /**
     * 名称
     */
    private String name;

    /**
     * 数值
     */
    private String num;

    /**
     * 比较
     */
    private String compare;

    /**
     * 同比
     */
    private String compareNum;
}
