package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 供应商基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_supplier")
public class Supplier extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    private String supplierName;

    /**
     * 供应商联系人
     */
    @Excel(name = "供应商联系人")
    private String supplierContactUser;

    /**
     * 供应商联系电话
     */
    @Excel(name = "供应商联系电话")
    private String supplierPhone;


}
