package com.ruoyi.wms.statistics.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.statistics.domain.OutBillStatistic;
import org.apache.ibatis.annotations.Param;

/**
 * 出库单信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-18
 */
public interface OutBillStatisticMapper extends BaseMapper<OutBill> {


    /**
     * 查询出库单信息列表
     *
     * @param outBill 出库单信息
     * @return 出库单信息集合
     */
    List<OutBillStatistic> select(@Param("ew") QueryWrapper<OutBillStatistic> outBill);

    /**
     * 查询出库单信息列表
     *
     * @param OutBillStatistic 出库统计信息
     * @return 出库单信息集合
     */
    List<OutBillStatistic> selectOutBillList(@Param("ew") QueryWrapper<OutBillStatistic> queryWrapper);
}
