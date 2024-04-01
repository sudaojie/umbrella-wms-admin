package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskDataInfo;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskGoodsInfo;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.vo.ListingDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 上架单详情Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
public interface ListingDetailMapper extends BaseMapper<ListingDetail> {


    /**
     * 查询上架单详情列表
     *
     * @param listingDetail 上架单详情
     * @return 上架单详情集合
     */
    List<ListingDetail> select(@Param("ew") QueryWrapper<ListingDetail> listingDetail);


    /**
     * 根据ids修改上架状态
     *
     * @param ids 入库单详情信息id集合
     * @return 操作结果数
     */
    int updateStatusByIds(@Param("username") String username, @Param("status") String status, @Param("ids") String[] ids);

    /**
     * 查看上架数据
     * @param listingDetailQuery  listingDetailQuery
     * @return 上架列表
     */
    List<ListingDetailVo> selectPutOnData(@Param("ew") QueryWrapper<ListingDetail> listingDetailQuery);

    /**
     * 查询wms已组盘数据信息列表
     * @param listingDetailQuery listingDetailQuery
     * @return list
     */
    List<WmsGroupDiskDataInfo> selectWmsGroupDiskDataInfoList(@Param("ew") QueryWrapper<ListingDetail> listingDetailQuery);

    /**
     * 查询wms已组盘托盘上货物信息列表
     * @param listingDetailQuery listingDetailQuery
     * @return list
     */
    List<WmsGroupDiskGoodsInfo> getGoodsInfoOnGroupTray(@Param("ew") QueryWrapper<ListingDetail> listingDetailQuery);

}
