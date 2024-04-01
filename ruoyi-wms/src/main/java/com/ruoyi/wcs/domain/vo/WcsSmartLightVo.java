package com.ruoyi.wcs.domain.vo;

import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author hewei
 * @date 2023/4/12 0012 11:44
 */
@Data
@Accessors(chain = true)
public class WcsSmartLightVo extends WcsSmartLightingDetailInfo implements Serializable {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备区域
     */
    private String deviceArea;

    /**
     * 是否选择
     */
    private Boolean choose;

    /**
     * 照明设备编号
     */
    private String smartLightId;

    /**
     * 全局执行开启或关闭标志位
     */
    private Boolean batchOpenCloseFlag;

}
