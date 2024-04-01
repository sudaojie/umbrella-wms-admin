package com.ruoyi.wms.stock.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import org.apache.ibatis.annotations.Param;

/**
 * 晾晒入库单Mapper接口
 *
 * @author nf
 * @date 2023-03-10
 */
public interface WmsDryInbillMapper extends BaseMapper<WmsDryInbill> {


    /**
     * 查询晾晒入库单列表
     *
     * @param wmsDryInbill 晾晒入库单
     * @return 晾晒入库单集合
     */
    List<WmsDryInbill> select(@Param("ew") QueryWrapper<WmsDryInbill> wmsDryInbill);

    /**
     * 根据入库单id查询关联的机件号
     * @param asList 入库单id
     * @return
     */
    List<WmsDryInbillGoods> selectPartsCodeByIds(@Param("ids")List<String> asList);
}
