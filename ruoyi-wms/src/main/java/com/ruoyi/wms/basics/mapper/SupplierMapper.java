package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Supplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 供应商基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface SupplierMapper extends BaseMapper<Supplier> {


    /**
     * 查询供应商基本信息列表
     *
     * @param supplier 供应商基本信息
     * @return 供应商基本信息集合
     */
    List<Supplier> select(@Param("ew") QueryWrapper<Supplier> supplier);

    /**
     * 根据供应商编码获取数据
     *
     * @param supplierCode 供应商编码
     * @return
     */
    Supplier selectDataByCode(@Param("supplierCode") String supplierCode);

    /**
     * 验证供应商编码唯一性
     *
     * @param supplier（包含车牌号，id主键）
     * @return
     */
    int checkCode(@Param("object") Supplier supplier);

    /**
     * 获取全部的供应商
     *
     * @return
     */
    List<Map> getSupplierData();
}
