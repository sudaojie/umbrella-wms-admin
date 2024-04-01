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
public class WcsPowerChartsVo {

    /**
     * 名称
     */
    private String name;

    /**
     * 总有功功率(kw)
     */
    private String totalActivePower;

    /**
     * A相有功功率
     */
    private String phaseActivePowerA;

    /**
     * B相有功功率
     */
    private String phaseActivePowerB;

    /**
     * C相有功功率
     */
    private String phaseActivePowerC;

    /**
     * 总无功功率(kvarh)
     */
    private String totalReactivePower;

    /**
     * A相无功功率
     */
    private String phaseReactivePowerA;

    /**
     * B相无功功率
     */
    private String phaseReactivePowerB;

    /**
     * C相无功功率
     */
    private String phaseReactivePowerC;

}
