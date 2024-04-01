package com.ruoyi.wms.nolist.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 无单上架待上架列Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-08
 */
public interface NolistWaitMapper extends BaseMapper<NolistWait> {


    /**
     * 查询无单上架待上架列列表
     *
     * @param nolistWait 无单上架待上架列
     * @return 无单上架待上架列集合
     */
    List<NolistWait> select(@Param("ew") QueryWrapper<NolistWait> nolistWait);

}
