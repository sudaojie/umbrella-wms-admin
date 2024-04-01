package com.ruoyi.wms.basics.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LocationMapVo {
    /**
     * 库位编码
     */
    private String locationCode;
    /**
     * 托盘编码
     */
    private String trayCode;
    /**
     * 库区编码
     */
    private String areaCode;
    /**
     * 货物编码
     */
    private String goodsCode;
    /**
     * 结束库位编码
     */
    private String endLocationCode;
    /**
     * 结束库区编码
     */
    private String endAreaCode;

    /**
     * 排序值
     */
    private Integer orderNum;

    /**
     * 库位类型(1.母库位  2.子库位)
     */
    private String locationType;

    /**
     * 库位朝向(1.左侧  2.右侧)
     */
    private String locationArrow;

    /**
     *
     */
    private String code;
}
