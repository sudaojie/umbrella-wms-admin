package com.ruoyi.wms.group.disk.data.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * wms已组盘数据信息对象
 *
 * @author ruoyi
 * @date 2023-04-19
 */
@Data
@Accessors(chain = true)
public class WmsGroupDiskDataInfo extends BaseEntity {

    /**
     * 托盘编号
     */
    @Excel(name = "托盘编号")
    private String trayCode;

    /**
     * 货物类型
     */
    @Excel(name = "货物类型")
    private String goodsType;

    /**
     * 货物实际数量
     */
    @Excel(name = "货物实际数量")
    private Long actualNum;

    /**
     * 组盘时间
     */
    @Excel(name = "组盘时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date groupDiskTime;

}
