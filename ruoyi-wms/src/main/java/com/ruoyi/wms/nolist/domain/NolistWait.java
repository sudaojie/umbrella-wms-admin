package com.ruoyi.wms.nolist.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 无单上架待上架列对象
 *
 * @author ruoyi
 * @date 2023-03-08
 */
@Data
@Accessors(chain = true)
@TableName("wms_nolist_wait")
public class NolistWait extends BaseEntity{


    /** 主键 */
    private String id;

    /** 托盘编号 */
    @Excel(name = "托盘编号")
    private String trayCode;

    /** 上架状态;(0-未上架 1-已上架 2-上架中) */
    @Excel(name = "上架状态;(0-未上架 1-已上架 2-上架中)")
    private String listingStatus;

    /**
     * 托盘现有的机件号
     */
    @TableField(exist = false)
    private List<String> partsCodes;

}
