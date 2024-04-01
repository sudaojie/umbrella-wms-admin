package com.ruoyi.wms.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存预警策略Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-13
 */
public interface WmsWarningConfigMapper extends BaseMapper<WmsWarningConfig> {


    /**
     * 查询库存预警策略列表
     *
     * @param wmsWarningConfig 库存预警策略
     * @return 库存预警策略集合
     */
    List<WmsWarningConfig> select(@Param("ew") QueryWrapper<WmsWarningConfig> wmsWarningConfig);

    /**
     * 根据参数键名获取预警策略配置
     * @param queryWrapper
     * @return
     */
    int selectConfigByValue(@Param("ew") QueryWrapper queryWrapper);
}
