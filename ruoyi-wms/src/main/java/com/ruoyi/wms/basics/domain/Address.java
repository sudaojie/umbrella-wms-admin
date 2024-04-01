package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 地址基本信息对象
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_address")
public class Address extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 所属单位
     */
    @Excel(name = "所属单位")
    private String company;

    /**
     * 收货人
     */
    @Excel(name = "收货人")
    private String receiver;

    /**
     * 手机号
     */
    @Excel(name = "手机号")
    private String mobilePhone;

    /**
     * 邮编
     */
    @Excel(name = "邮编")
    private String postalCode;

    /**
     * 行政区划
     */
    @Excel(name = "行政区划")
    private String province;

    /**
     * 地址
     */
    @Excel(name = "地址")
    private String address;


    @TableField(exist = false)
    private List<String> selected;

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }
}
