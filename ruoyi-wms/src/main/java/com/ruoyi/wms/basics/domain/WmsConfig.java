package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * wms参数配置对象
 *
 * @author ruoyi
 * @date 2023-02-23
 */
@Data
@Accessors(chain = true)
@TableName("wms_config")
public class WmsConfig extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 参数键名 */
    @Excel(name = "参数键名")
    private String wmsConfigKey;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String wmsConfigName;

    /** 参数键值 */
    @Excel(name = "参数键值")
    private String wmsConfigValue;

    /** 是否启用(0：启用 1：未启用) */
    @Excel(name = "是否启用(0：启用 1：未启用)")
    private String enableStatus;



}
