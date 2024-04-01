package com.ruoyi.wms.check.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 盘点配置对象
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_check_config")
public class CheckConfig extends BaseEntity{


    /** 主键ID */
    private String id;

    /** 库位编号 */
    @Excel(name = "库位编号")
    private String locationCode;

    /** 货物编号 */
    @Excel(name = "货物编号")
    private String goodsCode;

    /** 货物名称 */
    @Excel(name = "货物名称")
    private String goodsName;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    private String checkBillCode;



}
