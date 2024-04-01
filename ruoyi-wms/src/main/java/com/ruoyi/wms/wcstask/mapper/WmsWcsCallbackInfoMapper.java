package com.ruoyi.wms.wcstask.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WMS/WCS回调信息Mapper接口
 *
 * @author ruoyi
 * @date 2023-06-27
 */
public interface WmsWcsCallbackInfoMapper extends BaseMapper<WmsWcsCallbackInfo> {


    /**
     * 查询WMS/WCS回调信息列表
     *
     * @param wmsWcsCallbackInfo WMS/WCS回调信息
     * @return WMS/WCS回调信息集合
     */
    List<WmsWcsCallbackInfo> select(@Param("ew") QueryWrapper<WmsWcsCallbackInfo> wmsWcsCallbackInfo);

}
