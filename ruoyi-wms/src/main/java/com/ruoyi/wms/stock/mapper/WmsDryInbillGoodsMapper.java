package com.ruoyi.wms.stock.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 晾晒出入库单货物Mapper接口
 *
 * @author nf
 * @date 2023-03-10
 */
public interface WmsDryInbillGoodsMapper extends BaseMapper<WmsDryInbillGoods> {


    /**
     * 查询晾晒出入库单货物列表
     *
     * @param wmsDryInbillGoods 晾晒出入库单货物
     * @return 晾晒出入库单货物集合
     */
    List<WmsDryInbillGoods> select(@Param("ew") QueryWrapper<WmsDryInbillGoods> wmsDryInbillGoods);

}
