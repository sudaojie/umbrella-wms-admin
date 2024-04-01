package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统临时附件对象
 *
 * @author yangjie
 * @date 2022-10-28
 */
@Data
@Accessors(chain = true)
@TableName("sys_file_temp")
public class SysFileTemp extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 业务编号
     */
    private String buinessId;


    /**
     * 类型
     */
    @Excel(name = "类型")
    private String type;

    /**
     * 源文件名称
     */
    @Excel(name = "源文件名称")
    private String displayName;

    /**
     * 文件名称
     */
    @Excel(name = "文件名称")
    private String name;

    /**
     * 路径
     */
    @JsonIgnore
    @Excel(name = "路径")
    private String path;

    /**
     * 大小
     */
    @Excel(name = "大小")
    private Long size;

    /**
     * 排序值
     */
    @Excel(name = "排序值")
    private Long orderNo;

    /**
     * 附件访问路径
     */
    @TableField(exist = false)
    private String visitUrl;

}
