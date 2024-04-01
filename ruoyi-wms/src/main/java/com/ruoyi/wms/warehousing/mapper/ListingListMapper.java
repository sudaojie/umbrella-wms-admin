package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.warehousing.domain.ListingList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 上架单Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-02
 */
public interface ListingListMapper extends BaseMapper<ListingList> {


    /**
     * 查询上架单列表
     *
     * @param listingList 上架单
     * @return 上架单集合
     */
    List<ListingList> select(@Param("ew") QueryWrapper<ListingList> listingList);




}
