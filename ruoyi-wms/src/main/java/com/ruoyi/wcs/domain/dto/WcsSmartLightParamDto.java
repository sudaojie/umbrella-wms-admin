package com.ruoyi.wcs.domain.dto;

import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hewei
 * @date 2023/4/11 0011 09:32
 */
@Data
@Accessors(chain = true)
public class WcsSmartLightParamDto extends WcsSmartLightingDetailInfo {

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备区域
     */
    private String deviceArea;

    /**
     * 全局执行开启或关闭标志位
     */
    private Boolean batchOpenCloseFlag;

}
