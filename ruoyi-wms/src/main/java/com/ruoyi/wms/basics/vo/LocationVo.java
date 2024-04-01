package com.ruoyi.wms.basics.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 首页类基本信息对象
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Data
@Accessors(chain = true)
public class LocationVo {

    /**
     * 库位编码
     */
    @TableField(exist = false)
    private String locationCode;

    /**
     * 库位状态
     */
    @Excel(name = "库位状态")
    private String locationStatus;

}
