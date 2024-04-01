package com.ruoyi.wms.basics.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.wms.basics.vo.LocationListVo;
import lombok.Data;

/**
 * 晾晒区封装实体类
 */
@Data
public class SunCureAreaDto {

    @TableField(exist = false)
    private LocationListVo locationList;
}
