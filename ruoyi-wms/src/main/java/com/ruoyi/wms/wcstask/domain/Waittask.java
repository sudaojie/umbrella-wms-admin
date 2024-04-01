package com.ruoyi.wms.wcstask.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 启用AGV时，wcs等待执行任务列对象
 *
 * @author ruoyi
 * @date 2023-02-24
 */
@Data
@Accessors(chain = true)
@TableName("wms_wcs_waittask")
public class Waittask extends BaseEntity{


    /** 主键 */
    private String id;

    /** 状态（0-未执行；1-已执行） */
    @Excel(name = "状态", readConverterExp = "0=-未执行；1-已执行")
    private String taskStatus;

    /** 数据 */
    @Excel(name = "数据")
    private String taskData;

    /** 消费顺序 */
    @Excel(name = "消费顺序")
    private Long taskOrder;



}
