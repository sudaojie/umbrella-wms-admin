package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机件号记录Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-03
 */
public interface PartsMapper extends BaseMapper<Parts> {


    /**
     * 查询机件号记录列表
     *
     * @param parts 机件号记录
     * @return 机件号记录集合
     */
    List<Parts> select(@Param("ew") QueryWrapper<Parts> parts);


    /**
     * 根据机件号和id查询是否有重复数据
     *
     * @param parts
     * @return
     */
    int checkCode(@Param("object") InbillGoods parts);

    /**
     * 查询机件号记录展示列表
     *
     * @param queryWrapper
     * @return
     */
    List<Parts> findPartsList(@Param("ew") QueryWrapper<Parts> queryWrapper);

    /**
     * 级联获取物品类型
     *
     * @param parts
     * @return
     */
    List<Parts> getCategoryCode(@Param("p") Parts parts);

    /**
     * 初始化获取入库单号
     *
     * @return
     */
    List<InbillDetail> findInbillCode();

    /**
     * 修改机件号
     *
     * @param parts
     */
    void updateByOnlyCode(@Param("p") Parts parts);

    /**
     * 下载模板数据
     *
     * @param queryWrapper
     * @return
     */
    List<Parts> importPartsList(@Param("ew") QueryWrapper<Parts> queryWrapper);

    /**
     * 修改机件号
     *
     * @param parts
     */
    void updateByPartsCode(@Param("ew") Parts parts);

    /**
     * 校验唯一码是否重复
     *
     * @param parts
     * @return
     */
    int checkOnlyCode(@Param("ew") InbillGoods parts);

    /**
     * 根据ids获取打印数据
     *
     * @param ids
     * @return
     */
    List<Parts> listByIds(@Param("ids") List<String> ids);

    /**
     * 根据partCodes获取打印数据
     *
     * @param partCodes
     * @return
     */
    List<Parts> listByPartCodes(@Param("partCodes") String[] partCodes);


    /**
     * 根据查询条件获取打印数据
     *
     * @param queryWrapper
     * @return
     */
    List<Parts> selectLists(@Param("ew") QueryWrapper<Parts> queryWrapper);

    /**
     * 修改打印状态
     *
     * @param ids
     */
    void updatePartsPrint(@Param("ids") List<String> ids);

    /**
     * 新增初始化获取入库单号
     *
     * @return
     */
    List<InbillDetail> findAddInbillCode();

    /**
     * 新增查询机件号记录展示列表
     *
     * @param parts
     * @return
     */
    List<Parts> findAddPartsList(@Param("p") Parts parts);

    /**
     * 获取质保期数据
     *
     * @param queryWrapper
     * @return
     */
    List<Parts> findPeriodByCode(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 根据唯一码，找到货物保质期天数
     *
     * @param parts
     * @return
     */
    Parts getGoodsCodeByOnly(@Param("p") Parts parts);

    List<Parts> selectByInbillCode(@Param("inbillCode") String inbillCode);

    List<String> checkMultiCode(@Param("list") List<InbillGoods> inbillGoods, @Param("goodCode") String goodCode);

    List<String> checkOnlyMultiCode(@Param("list") List<InbillGoods> inbillGoods);
}
