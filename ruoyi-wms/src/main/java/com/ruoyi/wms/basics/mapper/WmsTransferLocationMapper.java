package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.WmsTransferLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 传输带库位信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-06-30
 */
public interface WmsTransferLocationMapper extends BaseMapper<WmsTransferLocation> {


    /**
     * 查询传输带库位信息列表
     *
     * @param wmsTransferLocation 传输带库位信息
     * @return 传输带库位信息集合
     */
    List<WmsTransferLocation> select(@Param("ew") QueryWrapper<WmsTransferLocation> wmsTransferLocation);

}
