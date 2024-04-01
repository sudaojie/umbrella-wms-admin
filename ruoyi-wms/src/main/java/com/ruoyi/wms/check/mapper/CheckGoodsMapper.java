package com.ruoyi.wms.check.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.CheckGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点货物单Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
public interface CheckGoodsMapper extends BaseMapper<CheckGoods> {


    /**
     * 查询库存盘点货物单列表
     *
     * @param checkGoods 库存盘点货物单
     * @return 库存盘点货物单集合
     */
    List<CheckGoods> select(@Param("ew") QueryWrapper<CheckGoods> checkGoods);

    /**
     * 查询要生成的盘点详情货物
     * @param checkBillCode
     * @return
     */
    List<CheckGoods> selectCheckGoodsList(@Param("checkBillCode") String checkBillCode);

    /**
     * 根据货物编码获取货物信息
     * @param queryWrapper
     * @return
     */
    List<CheckGoods> selectByGoodsCode(@Param("ew") QueryWrapper<CheckGoods> queryWrapper);

    /**
     * 根据盘点单号获取货物详情
     * @param queryWrapper
     * @return
     */
    List<CheckGoods> getCheckGoodsList(@Param("ew") QueryWrapper<CheckGoods> queryWrapper);

    /**
     * 根据盘点单删除盘点货物信息
     * @param checkBillCode
     */
    void updateGoodsByCode(@Param("checkBillCode") String checkBillCode);

    /**
     * 根据盘点单号获取盘点货物信息
     * @param queryWrapper
     * @return
     */
    List<CheckGoods> selectGoodsByCode(@Param("ew") QueryWrapper<CheckGoods> queryWrapper);
}
