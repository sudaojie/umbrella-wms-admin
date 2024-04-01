package com.ruoyi.wms.basics.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 巷道分类集合封装实体
 */
@Data
@Accessors(chain = true)
public class LocationListVo {

    /**
     * 上方细巷道类集合
     */
    @TableField(exist = false)
    private List<List<LocationVo>> upFine;

    /**
     * 下方细巷道类集合
     */
    @TableField(exist = false)
    private List<List<LocationVo>> downFine;

    /**
     * 普通巷道类集合
     */
    @TableField(exist = false)
    private List<List<LocationVo>> formal;
}
