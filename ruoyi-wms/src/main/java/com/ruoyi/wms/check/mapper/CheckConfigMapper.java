package com.ruoyi.wms.check.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.check.domain.CheckDetail;
import org.apache.ibatis.annotations.Param;

/**
 * 盘点配置Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
public interface CheckConfigMapper extends BaseMapper<CheckConfig> {


    /**
     * 查询盘点配置列表
     *
     * @param checkConfig 盘点配置
     * @return 盘点配置集合
     */
    List<CheckConfig> select(@Param("ew") QueryWrapper<CheckConfig> checkConfig);

    /**
     * 根据盘点单号，获取盘点配置信息
     * @param queryWrapper
     * @return
     */
    List<CheckDetail> selectCheckConfigByCode(@Param("ew") QueryWrapper<Check> queryWrapper);

    /**
     * 删除盘点配置
     * @param id
     */
    void delCheckConfig(@Param("id") String id);

    /**
     * 根据盘点编号删除盘点配置
     * @param checkBillCode
     */
    void updateConfigsByCode(@Param("checkBillCode") String checkBillCode);
}
