package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Warehouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 仓库基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-30
 */
public interface WarehouseMapper extends BaseMapper<Warehouse> {


    /**
     * 查询仓库基本信息列表
     *
     * @param warehouse 仓库基本信息
     * @return 仓库基本信息集合
     */
    List<Warehouse> select(@Param("ew") QueryWrapper<Warehouse> warehouse);

    /**
     * 验证仓库编码唯一性
     *
     * @param warehouse（包含仓库编码，id主键）
     * @return
     */
    int checkCode(@Param("object") Warehouse warehouse);

    /**
     * 验证仓库名称唯一性
     *
     * @param warehouse（包含仓库名称，id主键）
     * @return
     */
    int checkName(@Param("object") Warehouse warehouse);

    /**
     * 获取仓库信息
     *
     * @return 【{label：xx，value：xx}】
     */
    List<Map> getWarehouseData();

    /**
     * 根据仓库编码获取仓库信息
     */
    Warehouse selectDataByCode(@Param("warehouseCode") String warehouseCode);

}
