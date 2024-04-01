package com.ruoyi.wcs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsSmartLightingDetailInfo;
import com.ruoyi.wcs.domain.dto.WcsSmartLightParamDto;
import com.ruoyi.wcs.domain.vo.WcsSmartLightVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 智慧照明系统详情信息Mapper接口
 *
 * @author hewei
 * @date 2023-04-12
 */
@Mapper
public interface WcsSmartLightingDetailInfoMapper extends BaseMapper<WcsSmartLightingDetailInfo> {

    /**
     * 查询智慧照明系统详情信息列表
     *
     * @param wcsSmartLightParamDto 条件
     * @return 智慧照明系统列表集合
     */
    List<WcsSmartLightVo> queryList(@Param("wcsSmartLightParamDto") WcsSmartLightParamDto wcsSmartLightParamDto);

}
