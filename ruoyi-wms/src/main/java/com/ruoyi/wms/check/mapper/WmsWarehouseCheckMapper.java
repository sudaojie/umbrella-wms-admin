package com.ruoyi.wms.check.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckDetail;
import com.ruoyi.wms.check.domain.CheckGoods;
import com.ruoyi.wms.check.dto.CheckDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存盘点Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-15
 */
public interface WmsWarehouseCheckMapper extends BaseMapper<Check> {


    /**
     * 查询库存盘点列表
     *
     * @param wmsWarehouseCheck 库存盘点
     * @return 库存盘点集合
     */
    List<Check> select(@Param("ew") QueryWrapper<Check> wmsWarehouseCheck);

    /**
     * 获取全盘详细数据信息
     * @return
     */
    List<CheckDetail> getAllCheckbill(@Param("ew") QueryWrapper<CheckDetail> queryWrapper);

    /**
     * 查询库位列表
     * @param checkQueryWrapper
     * @return
     */
    List<CheckDetailVo> getLocationList(@Param("ew") QueryWrapper<Check> checkQueryWrapper);

    /**
     * 查询货物类型列表
     * @param checkQueryWrapper
     * @return
     */
    List<CheckGoods> getGoodsList(@Param("ew") QueryWrapper<Check> checkQueryWrapper);

    /**
     * 根据盘点单号，获取盘点抬头信息
     * @param queryWrapper
     * @return
     */
    Check selectCheckByCheckBillCode(@Param("ew") QueryWrapper<Check> queryWrapper);


}
