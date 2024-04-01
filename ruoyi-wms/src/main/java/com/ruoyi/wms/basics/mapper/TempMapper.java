package com.ruoyi.wms.basics.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.basics.domain.Temp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板配置Mapper接口
 *
 * @author ruoyi
 * @date 2023-01-09
 */
public interface TempMapper extends BaseMapper<Temp> {


    /**
     * 查询模板配置列表
     *
     * @param queryWrapper 模板配置
     * @return 模板配置集合
     */
    List<Temp> select(@Param("ew") QueryWrapper<Temp> queryWrapper);

    /**
     * 验证模板编码唯一性
     *
     * @param wmsWarehouseTemp（包含模板id，id主键）
     * @return
     */
    int checkTempId(@Param("object") Temp wmsWarehouseTemp);

    /**
     * 根据tempId查询模板信息
     *
     * @param tempId 模板id
     * @return
     */
    Temp selectTempByTempId(@Param("tempId") String tempId);
}
