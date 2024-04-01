package com.ruoyi.wcs.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * wcs电压电流信息采集Mapper接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Mapper
public interface WcsVoltageCurrentCollectInfoMapper extends BaseMapper<WcsVoltageCurrentCollectInfo> {


    /**
     * 查询wcs电压电流信息采集列表
     *
     * @param wcsVoltageCurrentCollectInfo wcs电压电流信息采集
     * @return wcs电压电流信息采集集合
     */
    List<WcsVoltageCurrentCollectInfo> select(@Param("ew") QueryWrapper<WcsVoltageCurrentCollectInfo> wcsVoltageCurrentCollectInfo);

}
