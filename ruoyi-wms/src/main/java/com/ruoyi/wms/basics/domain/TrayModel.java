package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 托盘规格对象
 *
 * @author ruoyi
 * @date 2023-02-21
 */
@Data
@Accessors(chain = true)
@TableName("wms_tray_model")
public class TrayModel extends BaseEntity{


    /** id主键 */
    private String id;

    /** 规格编号 */
    @Excel(name = "规格编号")
    private String trayModelCode;

    /** 规格名称 */
    @Excel(name = "规格名称")
    private String trayModelName;

    /** 长 */
    @Excel(name = "长(cm)")
    private BigDecimal modelLength;

    /** 宽 */
    @Excel(name = "宽(cm)")
    private BigDecimal modelWidth;

    /** 高 */
    @Excel(name = "高(cm)")
    private BigDecimal modelHeight;

    /** 容量 */
//    @Excel(name = "容量(m³)")
    private BigDecimal modelVolume;

    /** 限重 */
    @Excel(name = "限重(kg)")
    private BigDecimal modelWeight;

    @TableField(exist = false)
    private BigDecimal trayLength;

    @TableField(exist = false)
    private BigDecimal trayWidth;

    @TableField(exist = false)
    private BigDecimal trayHeight;

    @TableField(exist = false)
    private BigDecimal trayVolume;

    @TableField(exist = false)
    private BigDecimal trayLimitWeight;

}
