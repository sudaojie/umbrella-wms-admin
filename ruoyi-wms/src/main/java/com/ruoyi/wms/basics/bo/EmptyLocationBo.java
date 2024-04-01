package com.ruoyi.wms.basics.bo;

import lombok.Data;

/**
 * 空库位业务对象
 */
@Data
public class EmptyLocationBo {

    /**
     * 库位编号
     */
    private String locationCode;


    /**
     * 库位类型(1.母库位  2.子库位)
     */
    private String locationType;

    /**
     * 库位朝向(1.左侧  2.右侧)
     */
    private String locationArrow;


    /**
     * 排序号
     */
    private Integer orderNum;

}
