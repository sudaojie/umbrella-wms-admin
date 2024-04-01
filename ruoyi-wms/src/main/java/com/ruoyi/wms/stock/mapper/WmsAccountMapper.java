package com.ruoyi.wms.stock.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.stock.domain.WmsAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 库存台账Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-15
 */
public interface WmsAccountMapper extends BaseMapper<WmsAccount> {


    /**
     * 查询库存台账列表
     *
     * @param wmsWarehouseAccount 库存台账
     * @return 库存台账集合
     */
    List<WmsAccount> select(@Param("ew") QueryWrapper<WmsAccount> wmsWarehouseAccount);

}
