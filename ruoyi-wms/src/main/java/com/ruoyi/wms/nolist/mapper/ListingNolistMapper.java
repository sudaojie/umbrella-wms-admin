package com.ruoyi.wms.nolist.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.nolist.domain.ListingNolist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 无单上架Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
public interface ListingNolistMapper extends BaseMapper<ListingNolist> {


    /**
     * 查询无单上架列表
     *
     * @param listingNolist 无单上架
     * @return 无单上架集合
     */
    List<ListingNolist> select(@Param("ew") QueryWrapper<ListingNolist> listingNolist);

}
