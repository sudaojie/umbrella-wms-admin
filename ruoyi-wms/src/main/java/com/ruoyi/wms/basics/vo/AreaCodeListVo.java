package com.ruoyi.wms.basics.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 巷道集合类
 */
@Data
@Accessors(chain = true)
public class AreaCodeListVo {

    /**
     * 上方细巷道
     */
    @TableField(exist = false)
    private List<LocationVo> upFine;

    /**
     * 下方细巷道
     */
    @TableField(exist = false)
    private List<LocationVo> downFine;

    /**
     * 普通巷道
     */
    @TableField(exist = false)
    private List<LocationVo> formal;
}
