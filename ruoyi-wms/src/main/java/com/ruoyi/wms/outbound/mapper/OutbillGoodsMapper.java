package com.ruoyi.wms.outbound.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出库单货物Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-07
 */
public interface OutbillGoodsMapper extends BaseMapper<OutbillGoods> {


    /**
     * 查询出库单货物列表
     *
     * @param outbillGoods 出库单货物
     * @return 出库单货物集合
     */
    List<OutbillGoods> select(@Param("ew") QueryWrapper<OutbillGoods> outbillGoods);
    /**
     * 分组查询出库单货物列表
     *
     * @param queryWrapper 出库单货物
     * @return 出库单货物集合
     */
    List<OutbillGoods> selectListByGroup(@Param("ew")QueryWrapper<OutbillGoods> queryWrapper);
    /**
     * 分组查询出库单货物列表
     *
     * @param outbillGoods 出库单货物
     * @return 出库单货物集合
     */
    List<OutbillGoods> listGroup(@Param("outbillGoods")OutbillGoods outbillGoods);

    /**
     * 打印数据
     * @param outBillCode 出库单号
     * @return 打印数据
     */
    List<OutbillGoods> listByOutBillCode(@Param("outBillCode") String outBillCode);

    /**
     * 查询出库详情-PDA
     *
     * @param queryWrapper 出库单货物
     * @return
     */
    List<OutbillGoods> selectOutbillDetail(@Param("ew")QueryWrapper<OutbillGoods> queryWrapper);
}
