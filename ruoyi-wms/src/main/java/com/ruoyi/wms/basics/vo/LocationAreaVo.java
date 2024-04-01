package com.ruoyi.wms.basics.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页巷道基本信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class LocationAreaVo {

    /**
     * 库位编码
     */
    @TableField(exist = false)
    private List<List<LocationVo>> locationList;

    /**
     * 所属库区编码
     */
    @TableField(exist = false)
    private String areaCode;

}
