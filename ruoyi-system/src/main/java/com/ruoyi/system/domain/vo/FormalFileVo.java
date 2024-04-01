package com.ruoyi.system.domain.vo;

import lombok.Data;

/**
 * 保存文件参数对象
 *
 * @author yangjie
 * @date 2022-10-28
 */
@Data
public class FormalFileVo {

    /**
     * 业务编号
     */
    private String buniessId;

    /**
     * 临时文件表ID
     * 多个文件逗号（，）隔开
     */
    private String tempIds;

}
