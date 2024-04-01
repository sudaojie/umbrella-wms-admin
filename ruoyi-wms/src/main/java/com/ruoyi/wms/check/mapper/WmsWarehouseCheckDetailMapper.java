package com.ruoyi.wms.check.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.check.dto.CheckDetailVo;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点详情Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
public interface WmsWarehouseCheckDetailMapper extends BaseMapper<CheckDetail> {


    /**
     * 查询库存盘点详情列表
     *
     * @param wmsWarehouseCheckDetail 库存盘点详情
     * @return 库存盘点详情集合
     */
    List<CheckDetail> select(@Param("ew") QueryWrapper<CheckDetail> wmsWarehouseCheckDetail);

    /**
     * 修改盘点详情表托盘信息
     * @param checkBillCode
     * @return
     */
    int updateCheckDetailTrayInfo(@Param("checkBillCode") String checkBillCode);

    /**
     * 查询要生成的盘点详情
     * @param goodsCodeList
     * @return  areaCode, areaName, locationCode, locationName, trayCode, curtainNum
     */
    List<CheckDetail> selectCheckDetailList(@Param("goodsCodeList") List<String> goodsCodeList);

    /**
     * 修改未盘点数据的盘点数量为账面数量
     * @param checkBillCode
     * @return
     */
    int updateCheckDetailcheckNum(@Param("checkBillCode") String checkBillCode);

    /**
     * 查询账面数量、盘点数量
     * @param checkBillCode
     * @return curtainNum , checkNum
     */
    CheckDetailVo selectNum(@Param("checkBillCode") String checkBillCode);
    /**
     * 根据盘点编号，获取盘点详细信息
     * @param queryWrapper
     * @return
     */
    List<CheckDetail> selectCheckDetailByCode(@Param("ew") QueryWrapper<Check> queryWrapper);

    /**
     * 根据ID删除盘点单详情
     * @param id
     * @return
     */
    void delCheckDetail(@Param("id") String id);

    /**
     * 删除库存盘点详情
     * @param checkBillCode
     */
    void updateByCode(@Param("checkBillCode") String checkBillCode);

    /**
     * 库位下拉信息
     * @param queryWrapper
     * @return
     */
    List<CheckDetail> getLocationList(@Param("ew") QueryWrapper<Check> queryWrapper);

    /**
     * 查询库存明细列表
     * @param queryWrapper
     * @return
     */
    List<CheckDetail> getCheckDetail(@Param("ew") QueryWrapper<CheckDetail> queryWrapper);


    CheckDetail getStartCheckOne(@Param("ew") QueryWrapper<CheckDetail> queryWrapper);
}
