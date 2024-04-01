package com.ruoyi.wms.stock.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存预警策略对象
 *
 * @author ruoyi
 * @date 2023-03-13
 */
@Data
@Accessors(chain = true)
@TableName("wms_warning_config")
public class WmsWarningConfig extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String configName;

    /** 参数键名 */
    @Excel(name = "参数键名")
    private String configKey;

    /** 参数键值 */
    @Excel(name = "参数键值")
    private String configValue;

    /** 是否开启（0.开启 1.关闭） */
    @Excel(name = "是否开启", readConverterExp = "0=.开启,1=.关闭")
    private String configType;



}
