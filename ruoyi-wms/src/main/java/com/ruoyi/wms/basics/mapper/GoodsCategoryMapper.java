package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.GoodsCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 货物类别信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface GoodsCategoryMapper extends BaseMapper<GoodsCategory> {


    /**
     * 查询货物类别信息列表
     *
     * @param goodsCategory 货物类别信息
     * @return 货物类别信息集合
     */
    List<GoodsCategory> select(@Param("ew") QueryWrapper<GoodsCategory> goodsCategory);

    /**
     * 验证货物类别编码唯一性
     *
     * @param goodsCategory（包含库区编码，id主键）
     * @return
     */
    int checkCode(@Param("object") GoodsCategory goodsCategory);

    /**
     * 根据类别编码获取数据
     *
     * @param categoryCode 类别编码
     * @return
     */
    GoodsCategory selectDataByCode(@Param("categoryCode") String categoryCode);

    /**
     * 获取类别信息【{value：xx,label:xx}】
     *
     * @return
     */
    List<Map> getGoodscategoryData();
}
