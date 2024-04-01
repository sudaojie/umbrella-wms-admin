package com.ruoyi.wms.outbound.vo;

import lombok.Data;

/**
 * 机件码
 */
@Data
public class PartsCodeVo {
    /**
     * 机件码
     */
    private String partsCode;
    /**
     * 是否要组
     */
    private boolean status;

}
