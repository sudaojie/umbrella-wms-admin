package com.ruoyi.wms.move.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.move.domain.WmsMoveDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 移库单详情Mapper接口
 *
 * @author nf
 * @date 2023-03-01
 */
public interface WmsMoveDetailMapper extends BaseMapper<WmsMoveDetail> {


    /**
     * 查询移库单详情列表
     *
     * @param wmsMoveDetail 移库单详情
     * @return 移库单详情集合
     */
    List<WmsMoveDetail> select(@Param("ew") QueryWrapper<WmsMoveDetail> wmsMoveDetail);

}
