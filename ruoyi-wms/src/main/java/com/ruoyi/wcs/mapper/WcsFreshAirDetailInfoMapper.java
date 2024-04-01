package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsFreshAirDetailInfo;
import com.ruoyi.wcs.domain.vo.WcsFreshAirVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新风系统详情信息Mapper接口
 *
 * @author hewei
 * @date 2023-04-12
 */
public interface WcsFreshAirDetailInfoMapper extends BaseMapper<WcsFreshAirDetailInfo> {

    /**
     * 查询新风系统详情信息列表
     *
     * @param qw 条件
     * @return 新风系统列表集合
     */
    List<WcsFreshAirVo> query(@Param("ew")  QueryWrapper<WcsDeviceBaseInfo> qw);


    /**
     * 查询爆闪灯系统详情信息列表
     *
     * @return 爆闪灯系统列表集合
     */
    List<WcsFreshAirVo> queryExplosiveFlashList();


}
