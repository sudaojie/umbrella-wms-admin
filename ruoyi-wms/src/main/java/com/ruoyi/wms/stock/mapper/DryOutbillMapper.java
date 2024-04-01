package com.ruoyi.wms.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * 晾晒出库单Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-03
 */
public interface DryOutbillMapper extends BaseMapper<DryOutbill> {


    /**
     * 查询晾晒出库单列表
     *
     * @param wmsDryOutbill 晾晒出库单
     * @return 晾晒出库单集合
     */
    List<DryOutbill> select(@Param("ew") QueryWrapper<DryOutbill> wmsDryOutbill);

    /**
     * 获取库存总览货物信息信息
     * @return
     */
    List<Tblstock> getGoodsList(@Param("ew") QueryWrapper<Tblstock> queryWrapper);

    /**
     * 获取晾晒出库单列表信息
     * @param queryWrapper
     * @return
     */
    List<DryOutbill> selectByAreaCode(@Param("ew") QueryWrapper<DryOutbill> queryWrapper);

    /**
     * 点击开始按钮，晾晒出库
     * @param id
     * @return
     */
    boolean clickStart(@Param("ew") DryOutbill dryOutbill);

    /**
     * 通过partsCode将货物锁住
     * @param dryOutbillGoods
     * @return
     */
    boolean closeGoods(@Param("ew") DryOutbillGoods dryOutbillGoods);

    /**
     * 获取出库单下所有货物信息
     * @param dryOutbill
     * @return
     */
    List<DryOutbillGoods> selectByGoods(@Param("p") DryOutbill dryOutbill);

    /**
     * 查询晾晒出库单货物数据详细
     * @param queryWrapper
     * @return
     */
    List<DryOutbillGoods> getDryOutbillGoods(@Param("ew") QueryWrapper<DryOutbillGoods> queryWrapper);

    /**
     * 查询
     * @param dryOutbillGoods
     * @return
     */
    DryOutbillGoods selectByOutbillCode(@Param("ew") DryOutbillGoods dryOutbillGoods);

    /**
     * 修改出库单备注信息
     * @param dryOutbill
     */
    void updateRemarkById(@Param("ew") DryOutbill dryOutbill);

    /**
     * 修改晾晒货物状态已删除
     * @param dryOutbill
     */
    void delDryOutbillGoods(@Param("ew") DryOutbill dryOutbill);

    /**
     * 改变货物状态
     * @param dryOutbill
     */
    boolean clickGoodsStatus(@Param("ew") DryOutbill dryOutbill);
}
