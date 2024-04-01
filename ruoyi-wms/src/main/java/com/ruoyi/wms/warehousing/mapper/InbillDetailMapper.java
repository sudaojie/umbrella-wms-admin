package com.ruoyi.wms.warehousing.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入库单详情信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
public interface InbillDetailMapper extends BaseMapper<InbillDetail> {


    /**
     * 查询入库单详情信息列表
     *
     * @param inbillDetail 入库单详情信息
     * @return 入库单详情信息集合
     */
    List<InbillDetail> select(@Param("ew") QueryWrapper<InbillDetail> inbillDetail);

    /**
     * 删除不存在的入库单详情信息
     *
     * @param inBillCode 入库单号
     * @param ids        入库单详情信息id集合
     * @return 操作结果数
     */
    int deleteNoExist(@Param("username") String username, @Param("inBillCode") String inBillCode, @Param("ids") String[] ids);

    /**
     * 对比入库数量和预报数量,预报数量少于入库数量或等于入库数量(小于等于0说明入库数量大于等于预报数量，反之入库数量小于预报数量)
     * @param inBillCode 入库单号
     * @return
     */
    int compareNum(@Param("inBillCode")String inBillCode);

    /**
     * 根据供应商编码查询关联数据条数
     * @param supplierCode
     * @return
     */
    int selectDataBySupplierCode(@Param("supplierCode") String supplierCode);
}
