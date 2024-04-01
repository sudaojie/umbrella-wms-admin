package com.ruoyi.wms.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.Tblstock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存总览Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-06
 */
public interface TblstockMapper extends BaseMapper<Tblstock> {


    /**
     * 查询库存总览列表
     *
     * @param tblstock 库存总览
     * @return 库存总览集合
     */
    List<Tblstock> select(@Param("ew") QueryWrapper<Tblstock> tblstock);

    /**
     * 修改库存总览锁状态
     * @param goodsCodes
     * @return
     */
    int updateTblstockLockstatus(@Param("goodsCodes") List<String> goodsCodes);

    /**
     * 获取库存中托盘上的所有机件号
     * @param trayCode
     * @return
     */
    List<String> selectPartsCodeByTrayCode(@Param("trayCode") String trayCode);

    /**
     * 查询库存总览列表
     * @param queryWrapper
     * @return
     */
    List<Tblstock> selectTblstockList(@Param("ew") QueryWrapper<Tblstock> queryWrapper);

    /**
     * 查询库存详情列表
     * @param queryWrapper
     * @return
     */
    List<Tblstock> showTblstockDetail(@Param("ew") QueryWrapper<Tblstock> queryWrapper);

    /**
     * 本月期初库存
     * @return
     */
    String getOpeningInventoryOfThisMonth();

    /**
     * 本月期末库存
     * @return
     */
    String getEndingInventoryOfThisMonth();

    /**
     * 上月期初库存
     * @return
     */
    String getOpeningInventoryOfLastMonth();

    /**
     * 上月期末库存
     * @return
     */
    String getEndingInventoryOfLastMonth();

    /**
     * 本月出库数量
     * @return
     */
    String getOutGoodsNumOfThisMonth();

    /**
     * 上月出库数量
     * @return
     */
    String getOutGoodsNumOfLastMonth();
}
