package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.check.domain.CheckRealitygoods;
import com.ruoyi.wms.group.disk.data.domain.vo.WmsUnGroupGoodsInfoVO;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入库单货物Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
public interface InbillGoodsMapper extends BaseMapper<InbillGoods> {


    /**
     * 查询入库单货物列表
     *
     * @param inbillGoods 入库单货物
     * @return 入库单货物集合
     */
    List<InbillGoods> select(@Param("ew") QueryWrapper<InbillGoods> inbillGoods);


    /**
     * 查询入库单下未组盘的货物数量
     * @param inBillCode
     * @return
     */
    int selectInBillNotTrayCount(@Param("inBillCode") String inBillCode);


    /**
     * 查询数据
     * @param onlyCodes
     * @return
     */
    List<InbillGoods> selectByOnlyCodes(@Param("onlyCodes") List<String> onlyCodes);

    /**
     * 删除详情数据
     * @param username
     * @param inBillCode
     */
    void deleteByInBillCode(@Param("username") String username,@Param("inBillCode") String inBillCode);

    /**
     * 查询 PDA 未组盘数据信息列表
     * @return list
     */
    List<WmsUnGroupGoodsInfoVO> selectUnGroupDiskGoodsList();


    /**
     * 根据机件号获取货物信息
     * @param inbillGoods
     * @return
     */
    List<CheckRealitygoods> getGoodsInfo(@Param("ew") LambdaQueryWrapper<InbillGoods> inbillGoods);

    /**
     * 查询列表
     * @param inbillGoods
     * @return
     */
    List<InbillGoods> selectListInbillGoods(@Param("ew") QueryWrapper<InbillGoods> inbillGoods);


    /**
     * 根据id查询机件号
     *
     * @param inbillGoods 入库单货物
     * @return 入库单货物对象
     */
    InbillGoods selectByIdGoods(@Param("ew") QueryWrapper<InbillGoods> inbillGoods);
}
