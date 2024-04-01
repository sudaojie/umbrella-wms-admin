package com.ruoyi.wms.check.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点调整单详情Mapper接口
 *
 * @author nf
 * @date 2023-03-23
 */
public interface CheckAdjustDetailMapper extends BaseMapper<CheckAdjustDetail> {


    /**
     * 查询库存盘点调整单详情列表
     *
     * @param checkAdjustDetail 库存盘点调整单详情
     * @return 库存盘点调整单详情集合
     */
    List<CheckAdjustDetail> select(@Param("ew") QueryWrapper<CheckAdjustDetail> checkAdjustDetail);

}
