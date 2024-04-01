package com.ruoyi.wms.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 晾晒出库单货物Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-05
 */
public interface DryOutbillGoodsMapper extends BaseMapper<DryOutbillGoods> {


    /**
     * 查询晾晒出库单货物列表
     *
     * @param DryOutbillGoods 晾晒出库单货物
     * @return 晾晒出库单货物集合
     */
    List<DryOutbillGoods> select(@Param("ew") QueryWrapper<DryOutbillGoods> DryOutbillGoods);

    /**
     * 根据dry_outbill_code获取出库单货物信息
     * @param dryOutbillCode
     * @return
     */
    List<DryOutbillGoods> selectByCode(@Param("dryOutbillCode") String dryOutbillCode);

    /**
     * 清除dry_outbill_code绑定
     * @param wmsDryOutbill
     * @return
     */
    boolean deleteByCode(@Param("ew") DryOutbill wmsDryOutbill);

    /**
     * 初始化获取货区信息
     * @return
     */
    List<DryOutbillGoods> getAreaData();

    /**
     * 查询晾晒出库单详情列表
     * @param dryOutbillGoods
     * @return
     */
    List<DryOutbillGoods> selectGoodsDetailList(@Param("ew") DryOutbillGoods dryOutbillGoods);
}
