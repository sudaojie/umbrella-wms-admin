package com.ruoyi.wms.check.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.CheckAdjust;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点调整单Mapper接口
 *
 * @author nf
 * @date 2023-03-23
 */
public interface CheckAdjustMapper extends BaseMapper<CheckAdjust> {


    /**
     * 查询库存盘点调整单列表
     *
     * @param checkAdjust 库存盘点调整单
     * @return 库存盘点调整单集合
     */
    List<CheckAdjust> select(@Param("ew") QueryWrapper<CheckAdjust> checkAdjust);

}
