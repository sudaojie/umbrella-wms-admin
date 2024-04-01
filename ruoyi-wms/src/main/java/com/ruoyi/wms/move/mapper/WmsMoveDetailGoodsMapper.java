package com.ruoyi.wms.move.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.move.domain.WmsMoveDetailGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 移库单详情货物Mapper接口
 *
 * @author nf
 * @date 2023-03-01
 */
public interface WmsMoveDetailGoodsMapper extends BaseMapper<WmsMoveDetailGoods> {


    /**
     * 查询移库单详情货物列表
     *
     * @param wmsMoveDetailGoods 移库单详情货物
     * @return 移库单详情货物集合
     */
    List<WmsMoveDetailGoods> select(@Param("ew") QueryWrapper<WmsMoveDetailGoods> wmsMoveDetailGoods);

}
