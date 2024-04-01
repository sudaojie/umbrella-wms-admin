package com.ruoyi.wms.basics.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.wms.basics.vo.LocationVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页类理货区信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
public class TallyAreaDto {

    /** 库位编码 */
    @TableField(exist = false)
    private List<List<LocationVo>> locationList;

    /**
     * 所属库区类型
     */
    @TableField(exist = false)
    private String areaType;

}
