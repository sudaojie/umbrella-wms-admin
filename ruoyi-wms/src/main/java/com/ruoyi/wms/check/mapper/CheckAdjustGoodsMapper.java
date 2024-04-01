package com.ruoyi.wms.check.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.CheckAdjustGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点调整详情货物Mapper接口
 *
 * @author nf
 * @date 2023-03-23
 */
public interface CheckAdjustGoodsMapper extends BaseMapper<CheckAdjustGoods> {


    /**
     * 查询库存盘点调整详情货物列表
     *
     * @param checkAdjustGoods 库存盘点调整详情货物
     * @return 库存盘点调整详情货物集合
     */
    List<CheckAdjustGoods> select(@Param("ew") QueryWrapper<CheckAdjustGoods> checkAdjustGoods);

    /**
     * 查询盘盈数据
     * @param checkBillCode
     * @return  partsCode, onlyCode, trayCode,goodsCode, goodsName, model, measureUnit
     */
    List<CheckAdjustGoods> selectProfitData(@Param("checkBillCode") String checkBillCode);

    /**
     * 查询盘亏数据
     * @param checkBillCode
     * @return partsCode, onlyCode, trayCode,goodsCode, goodsName, model, measureUnit
     */
    List<CheckAdjustGoods> selectLossData(@Param("checkBillCode") String checkBillCode);
}
