package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模板配置对象
 *
 * @author ruoyi
 * @date 2023-01-09
 */
@Data
@Accessors(chain = true)
@TableName("wms_warehouse_temp")
public class Temp extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 模板ID
     */
    @Excel(name = "模板ID")
    private String tempId;

    /**
     * 模板名称
     */
    @Excel(name = "模板名称")
    private String tempName;

    /**
     * 所属模块
     */
    @Excel(name = "所属模块")
    private String module;

    /**
     * 文件名称
     */
    @Excel(name = "文件名称")
    private String fileName;

    /**
     * 文件主键
     */
    private String fileKey;


}
