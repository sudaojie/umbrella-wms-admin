package com.ruoyi.wms.outbound.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.outbound.domain.OutBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出库单信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-07
 */
public interface OutBillMapper extends BaseMapper<OutBill> {


    /**
     * 查询出库单信息列表
     *
     * @param outBill 出库单信息
     * @return 出库单信息集合
     */
    List<OutBill> select(@Param("ew") QueryWrapper<OutBill> outBill);

    /**
     * 查询出库单信息
     *
     * @param id 主键
     * @return 出库单信息
     */
    OutBill selectData(@Param("id")String id);
}
