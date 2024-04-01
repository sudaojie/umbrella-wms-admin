package com.ruoyi.wms.move.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 移库单Mapper接口
 *
 * @author nf
 * @date 2023-03-01
 */
public interface WmsMoveListMapper extends BaseMapper<WmsMoveList> {


    /**
     * 查询移库单列表
     *
     * @param wmsMoveList 移库单
     * @return 移库单集合
     */
    List<WmsMoveList> select(@Param("ew") QueryWrapper<WmsMoveList> wmsMoveList);

}
