package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsFreshAirCollectInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新风系统温湿度采集信息Mapper接口
 *
 * @author hewei
 * @date 2023-04-12
 */
public interface WcsFreshAirCollectInfoMapper extends BaseMapper<WcsFreshAirCollectInfo> {


    /**
     * 查询新风系统温湿度采集信息列表
     *
     * @param wcsFreshAirCollectInfo 新风系统温湿度采集信息
     * @return 新风系统温湿度采集信息集合
     */
    List<WcsFreshAirCollectInfo> select(@Param("ew") QueryWrapper<WcsFreshAirCollectInfo> wcsFreshAirCollectInfo);

}
