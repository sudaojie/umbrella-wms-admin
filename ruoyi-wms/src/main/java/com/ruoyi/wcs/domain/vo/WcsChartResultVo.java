package com.ruoyi.wcs.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hewei
 * @date 2023/4/11 0011 09:26
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WcsChartResultVo {

    /**
     * x轴
     */
    private List<String> xAxisList;

    /**
     * y轴
     */
    private List<String> yAxisList;

    private List<String> secondaryAxisList;
    private List<String> thirdAxisList;
    private List<String> fourthAxisList;
    private List<String> fifthAxisList;
    private List<String> sixthAxisList;
    private List<String> seventhAxisList;
    private List<String> eighthAxisList;

}
