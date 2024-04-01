package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.WmsConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * wms参数配置Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-23
 */
public interface WmsConfigMapper extends BaseMapper<WmsConfig> {


    /**
     * 查询wms参数配置列表
     *
     * @param wmsConfig wms参数配置
     * @return wms参数配置集合
     */
    List<WmsConfig> select(@Param("ew") QueryWrapper<WmsConfig> wmsConfig);

    /**
     * 校验参数键名是否重复
     * @param wmsConfig
     * @return
     */
    List<WmsConfig> checkData(@Param("ew") WmsConfig wmsConfig);
}
