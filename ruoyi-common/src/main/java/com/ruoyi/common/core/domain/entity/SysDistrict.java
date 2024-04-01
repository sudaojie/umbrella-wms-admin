package com.ruoyi.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 行政区划对象
 *
 * @author hewei
 * @date 2023-01-04
 */
@Data
@Accessors(chain = true)
@TableName("sys_district")
public class SysDistrict extends BaseEntity{


    /** 编号 */
    private String id;

    /** 名称 */
    @Excel(name = "名称")
    private String districtName;

    /** 父 ID */
    @Excel(name = "父 ID")
    private String pId;

    /** 拼音首字母 */
    @Excel(name = "拼音首字母")
    private String initial;

    /** 拼音首字母集合 */
    @Excel(name = "拼音首字母集合")
    private String initials;

    /** 拼音 */
    @Excel(name = "拼音")
    private String pinyin;

    /** 附加说明 */
    @Excel(name = "附加说明")
    private String extra;

    /** 行政级别 */
    @Excel(name = "行政级别")
    private String suffix;

    /** 行政代码 */
    @Excel(name = "行政代码")
    private String code;

    /** 区号 */
    @Excel(name = "区号")
    private String areaCode;

    /** 排序 */
    @Excel(name = "排序")
    private Integer orderNo;



}
