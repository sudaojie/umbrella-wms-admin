package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysDistrict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 行政区划Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-04
 */
public interface SysDistrictMapper extends BaseMapper<SysDistrict> {


    /**
     * 查询行政区划列表
     *
     * @param sysDistrict 行政区划
     * @return 行政区划集合
     */
    List<SysDistrict> select(@Param("ew") SysDistrict sysDistrict);

}
