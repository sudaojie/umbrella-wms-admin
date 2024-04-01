package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 货物信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface GoodsInfoMapper extends BaseMapper<GoodsInfo> {


    /**
     * 查询货物信息列表
     *
     * @param goodsInfo 货物信息
     * @return 货物信息集合
     */
    List<GoodsInfo> select(@Param("ew") QueryWrapper<GoodsInfo> goodsInfo);

    /**
     * 验证货物编码唯一性
     *
     * @param goodsInfo（包含货物编码，id主键）
     * @return
     */
    int checkCode(@Param("object") GoodsInfo goodsInfo);

    /**
     * 根据货物编码获取数据
     *
     * @param goodsCode 货物编码
     * @return
     */
    GoodsInfo selectDataByCode(@Param("goodsCode") String goodsCode);

    /**
     *
     * @param categoryCode
     * @return
     */
    int selectDataByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     *  根据供应商编码查询关联数据条数（废弃）
     * @param supplierCode
     * @return
     */
    int selectDataBySupplierCode(@Param("supplierCode") String supplierCode);

    /**
     * 获取入库货物信息
     * @param queryWrapper
     * @return
     */
    List<GoodsInfo> selectGoodsInfoList(@Param("ew") QueryWrapper<GoodsInfo> queryWrapper);

    /**
     * 根据库区编码查询关联数据条数
     * @param goodsInfo
     * @return
     */
    int selectByAreaId(@Param("object") GoodsInfo goodsInfo);
}
