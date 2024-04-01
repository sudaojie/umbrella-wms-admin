package com.ruoyi.wms.check.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.CheckRealitygoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点实盘货物单Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
public interface CheckRealitygoodsMapper extends BaseMapper<CheckRealitygoods> {


    /**
     * 查询库存盘点实盘货物单列表
     *
     * @param checkRealitygoods 库存盘点实盘货物单
     * @return 库存盘点实盘货物单集合
     */
    List<CheckRealitygoods> select(@Param("ew") QueryWrapper<CheckRealitygoods> checkRealitygoods);

}
