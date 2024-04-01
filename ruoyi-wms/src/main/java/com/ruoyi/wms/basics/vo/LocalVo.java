package com.ruoyi.wms.basics.vo;

import com.ruoyi.wms.basics.dto.SunCureAreaDto;
import com.ruoyi.wms.basics.dto.TallyAreaDto;
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
public class LocalVo {

    /**
     * 理货区封装实体类
     */
    private TallyAreaDto tallyArea;

    /**
     * 晾晒区封装实体类
     */
    private SunCureAreaDto sunCureArea;

}
