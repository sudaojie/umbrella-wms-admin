package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 货物类别信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_goods_category")
public class GoodsCategory extends BaseEntity{


    /** 编号 */
    private String id;

    /** 类别编码 */
    @Excel(name = "类别编码")
    private String categoryCode;

    /** 类别名称 */
    @Excel(name = "类别名称")
    private String categoryName;



}
