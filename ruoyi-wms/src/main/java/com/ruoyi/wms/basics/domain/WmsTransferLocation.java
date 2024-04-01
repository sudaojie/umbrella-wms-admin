package com.ruoyi.wms.basics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 传输带库位信息对象
 *
 * @author ruoyi
 * @date 2023-06-30
 */
@Data
@Accessors(chain = true)
@TableName("wms_transfer_location")
public class WmsTransferLocation extends BaseEntity {


    /**
     * 主键
     */
    private String id;

    /**
     * 传输带库位编码
     */
    @Excel(name = "传输带库位编码")
    private String tranferLocationCode;

    /**
     * 传输带所属库区
     */
    @Excel(name = "传输带所属库区")
    private String areaCode;

    /**
     * 传输带朝向(1.左侧  2.右侧)
     */
    @Excel(name = "传输带朝向(1.左侧  2.右侧)")
    private String tranferLocationArrow;


}
