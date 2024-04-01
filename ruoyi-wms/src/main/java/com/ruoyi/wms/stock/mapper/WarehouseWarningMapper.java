package com.ruoyi.wms.stock.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.WarehouseWarning;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 库存总览Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-10
 */
public interface WarehouseWarningMapper extends BaseMapper<WarehouseWarning> {


    /**
     * 查询库存总览列表
     *
     * @param wmsWarehouseTblstock 库存总览
     * @return 库存总览集合
     */
    List<WarehouseWarning> select(@Param("ew") QueryWrapper<WarehouseWarning> wmsWarehouseTblstock);

    /**
     * 可用数量查询
     * @param warning
     * @return
     */
    int selectByGoodsCode(@Param("ew") QueryWrapper<WarehouseWarning> warning);

    /**
     * 查询库存有效期列表
     * @param queryWrapper
     * @return
     */
    List<WarehouseWarning> selectWmsValidityList(@Param("ew") QueryWrapper<WarehouseWarning> queryWrapper);

    /**
     * 获取策略中有效期配置
     * @param queryWrapper
     * @return
     */
    WmsWarningConfig selectConfig(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 查询滞压预警列表
     * @param wrapper
     * @return
     */
    List<WarehouseWarning> selectDetainedList(@Param("ew") QueryWrapper wrapper);
}
