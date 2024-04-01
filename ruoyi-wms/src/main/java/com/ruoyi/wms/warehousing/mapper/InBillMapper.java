package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.warehousing.domain.InBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入库单信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
public interface InBillMapper extends BaseMapper<InBill> {


    /**
     * 查询入库单信息列表
     *
     * @param inBill 入库单信息
     * @return 入库单信息集合
     */
    List<InBill> select(@Param("ew") QueryWrapper<InBill> inBill);

    /**
     * 根据入库单号修改状态
     *
     * @return 操作结果数
     */
    int updateStatusByInBillCode(@Param("username") String username, @Param("status") String status, @Param("inBillCode") String inBillCode);

}
