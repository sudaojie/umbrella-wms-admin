package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 货物类型托盘取盘回盘策略配置对象
 *
 * @author ruoyi
 * @date 2023-02-24
 */
@Data
@Accessors(chain = true)
@TableName("wms_tactics_config")
public class WmsTacticsConfig extends BaseEntity{


    /** 主键 */
    private String id;

    /** 策略(0-平均分配；1-集中堆放) */
    @Excel(name = "策略(0-平均分配；1-集中堆放)")
    private String tactics;

    /** 集中堆放策略的库区顺序 */
    @Excel(name = "集中堆放策略的库区顺序")
    private String tacticsContent;

    /** 库区顺序数组 */
    @TableField(exist = false)
    private String[] arrTacticsContent;

    /** 库区编码 */
    @TableField(exist = false)
    private String warehouseAreaCode;

    /** 是否启用 */
    @TableField(exist = false)
    private String enableStatus;


}
