package com.ruoyi.wcs.domain.vo;

import com.ruoyi.common.annotation.Excel;
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
public class WcsVoltageCurrentChartsVo {

    /**
     * 名称
     */
    private String name;

    /**
     * A相电压(V)
     */
    private String phaseVoltageA;

    /**
     * B相电压(V)
     */
    private String phaseVoltageB;

    /**
     * C相电压(V)
     */
    private String phaseVoltageC;

    /**
     * A相电流(A)
     */
    private String phaseCurrentA;

    /**
     * B相电流(A)
     */
    private String phaseCurrentB;

    /**
     * C相电流(A)
     */
    private String phaseCurrentC;

}
