package com.ruoyi.wms.statistics.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.statistics.domain.InBillStatistic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 入库单信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-17
 */
public interface InBillStatisticMapper extends BaseMapper<InBillStatistic> {


    /**
     * 查询入库单信息列表
     *
     * @param inBill 入库单信息
     * @return 入库单信息集合
     */
    List<InBillStatistic> select(@Param("ew") QueryWrapper<InBillStatistic> inBill);

    /**
     * 查询入库统计信息列表
     * @param queryWrapper
     * @return
     */
    List<InBillStatistic> selectInBillList(@Param("ew") QueryWrapper<InBillStatistic> queryWrapper);
}
