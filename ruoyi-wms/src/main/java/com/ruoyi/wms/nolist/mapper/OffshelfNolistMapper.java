package com.ruoyi.wms.nolist.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.nolist.domain.OffshelfNolist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 无单下架Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
public interface OffshelfNolistMapper extends BaseMapper<OffshelfNolist> {


    /**
     * 查询无单下架列表
     *
     * @param offshelfNolist 无单下架
     * @return 无单下架集合
     */
    List<OffshelfNolist> select(@Param("ew") QueryWrapper<OffshelfNolist> offshelfNolist);

}
