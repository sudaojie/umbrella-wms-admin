package com.ruoyi.wms.move.domain;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 移库单对象
 *
 * @author nf
 * @date 2023-03-01
 */
@Data
@Accessors(chain = true)
@TableName("wms_move_list")
public class WmsMoveList extends BaseEntity{


    /** 主键 */
    private String id;

    /** 移库单号 */
    @Excel(name = "移库单号")
    private String moveCode;

    /** 移库状态 */
    @Excel(name = "移库状态")
    private String moveStatus;


    /** 移库单详情信息 */
    @TableField(exist = false)
    private List<WmsMoveDetail> wmsMoveDetailList;


    public List<WmsMoveDetail> getWmsMoveDetailList()
    {
        return wmsMoveDetailList;
    }

    public void setWmsMoveDetailList(List<WmsMoveDetail> wmsMoveDetailList)
    {
        this.wmsMoveDetailList = wmsMoveDetailList;
    }


}
