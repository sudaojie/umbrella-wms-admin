package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 地址基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-31
 */
public interface AddressMapper extends BaseMapper<Address> {


    /**
     * 查询地址基本信息列表
     *
     * @param address 地址基本信息
     * @return 地址基本信息集合
     */
    List<Address> select(@Param("ew") QueryWrapper<Address> address);

    /**
     * 获取地址信息
     *
     * @return 【{label：xx，value：xx}】
     */
    List<Map> getAddressList();
}
