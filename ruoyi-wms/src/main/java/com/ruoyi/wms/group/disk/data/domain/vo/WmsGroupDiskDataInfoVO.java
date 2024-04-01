package com.ruoyi.wms.group.disk.data.domain.vo;

import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskDataInfo;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskGoodsInfo;
import lombok.Data;

import java.util.List;

/**
 * @author hewei
 * @date 2023/4/19 0019 16:11
 */
@Data
public class WmsGroupDiskDataInfoVO extends WmsGroupDiskDataInfo {

    /**
     * 托盘货物列表
     */
    private List<WmsGroupDiskGoodsInfo> list;

}
