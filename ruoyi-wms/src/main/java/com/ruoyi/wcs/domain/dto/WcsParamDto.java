package com.ruoyi.wcs.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hewei
 * @date 2023/4/11 0011 09:32
 */
@Data
@Accessors(chain = true)
public class WcsParamDto {

    /**
     * 设备编号
     */
    private String id;

    /**
     * 类型
     */
    private String type;

    /**
     * 起始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页码
     */
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageNum;

    /**
     * 分页大小
     */
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageSize;

}
