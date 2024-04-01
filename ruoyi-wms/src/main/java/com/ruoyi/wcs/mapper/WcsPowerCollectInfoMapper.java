package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * wcs电压电流信息采集Mapper接口
 *
 * @author ruoyi
 * @date 2023-04-10
 */
@Mapper
public interface WcsPowerCollectInfoMapper extends BaseMapper<WcsPowerCollectInfo> {


    /**
     * 查询wcs电压电流信息采集列表
     *
     * @param wcsPowerCollectInfo wcs电压电流信息采集
     * @return wcs电压电流信息采集集合
     */
    List<WcsPowerCollectInfo> select(@Param("ew") QueryWrapper<WcsPowerCollectInfo> wcsPowerCollectInfo);

}
