package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.wms.enums.ListingEnum;
import com.ruoyi.wms.warehousing.domain.ListingList;
import com.ruoyi.wms.warehousing.mapper.ListingListMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 上架单Service接口
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@Slf4j
@Service
public class ListingListService extends ServiceImpl<ListingListMapper, ListingList> {

    @Autowired
    private ListingListMapper listingListMapper;

    /**
     * 查询上架单
     *
     * @param id 上架单主键
     * @return 上架单
     */
    public ListingList selectListingListById(String id) {
        QueryWrapper<ListingList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return listingListMapper.selectOne(queryWrapper);
    }

    /**
     * 根据上架单号查询上架单
     *
     * @param listingCode 上架单号
     * @return 上架单
     */
    public ListingList selectByListingCode(String listingCode) {
        QueryWrapper<ListingList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("listing_code", listingCode);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.last("limit 1");
        return listingListMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询上架单
     *
     * @param ids 上架单 IDs
     * @return 上架单
     */
    public List<ListingList> selectListingListByIds(String[] ids) {
        QueryWrapper<ListingList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return listingListMapper.selectList(queryWrapper);
    }

    /**
     * 查询上架单列表
     *
     * @param listingList 上架单
     * @return 上架单集合
     */
    public List<ListingList> selectListingListList(ListingList listingList) {
        QueryWrapper<ListingList> queryWrapper = getQueryWrapper(listingList);
        return listingListMapper.select(queryWrapper);
    }

    /**
     * 新增上架单
     *
     * @param listingList 上架单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingList insertListingList(ListingList listingList) {
        listingList.setId(IdUtil.simpleUUID());
        listingList.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        listingListMapper.insert(listingList);
        return listingList;
    }

    /**
     * 修改上架单
     *
     * @param listingList 上架单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ListingList updateListingList(ListingList listingList) {
        listingListMapper.updateById(listingList);
        return listingList;
    }

    /**
     * 批量删除上架单
     *
     * @param ids 需要删除的上架单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingListByIds(String[] ids) {
        List<ListingList> listingLists = new ArrayList<>();
        for (String id : ids) {
            ListingList listingList = new ListingList();
            listingList.setId(id);
            listingList.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            listingLists.add(listingList);
        }
        return super.updateBatchById(listingLists) ? 1 : 0;
    }

    /**
     * 删除上架单信息
     *
     * @param id 上架单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteListingListById(String id) {
        ListingList listingList = new ListingList();
        listingList.setId(id);
        listingList.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return listingListMapper.updateById(listingList);
    }

    public QueryWrapper<ListingList> getQueryWrapper(ListingList listingList) {
        QueryWrapper<ListingList> queryWrapper = new QueryWrapper<>();
        if (listingList != null) {
            listingList.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", listingList.getDelFlag());
            //上架单号
            if (StrUtil.isNotEmpty(listingList.getListingCode())) {
                queryWrapper.eq("listing_code", listingList.getListingCode());
            }
            //入库单号
            if (StrUtil.isNotEmpty(listingList.getInBillCode())) {
                queryWrapper.eq("in_bill_code", listingList.getInBillCode());
            }
            //批次
            if (StrUtil.isNotEmpty(listingList.getCharg())) {
                queryWrapper.eq("charg", listingList.getCharg());
            }
        }
        return queryWrapper;
    }

    /**
     * 根据入库单号查询上架单
     *
     * @param inBillCode 入库单号
     * @return 上架单
     */
    public ListingList selectByInBillCode(String inBillCode) {
        QueryWrapper<ListingList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("in_bill_code", inBillCode);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.last("limit 1");
        return listingListMapper.selectOne(queryWrapper);
    }
}
