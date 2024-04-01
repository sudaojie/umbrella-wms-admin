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
public class WcsEnergyChartsVo {

    /**
     * 名称
     */
    private String name;

    /**
     * 数值
     */
    private String value;

}
