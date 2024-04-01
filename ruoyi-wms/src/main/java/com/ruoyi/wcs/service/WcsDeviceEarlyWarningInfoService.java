package com.ruoyi.wcs.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.domain.WcsDeviceEarlyWarningInfo;
import com.ruoyi.wcs.domain.dto.WcsDeviceEarlyWarningFormDto;
import com.ruoyi.wcs.mapper.WcsDeviceEarlyWarningInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 设备预警信息Service接口
 *
 * @author hewei
 * @date 2023-04-17
 */
@Slf4j
@Service
public class WcsDeviceEarlyWarningInfoService extends ServiceImpl<WcsDeviceEarlyWarningInfoMapper, WcsDeviceEarlyWarningInfo> {

    @Autowired
    private WcsDeviceEarlyWarningInfoMapper wcsDeviceEarlyWarningInfoMapper;

    /**
     * 查询设备预警信息列表
     *
     * @param wcsDeviceEarlyWarningFormDto wcsDeviceEarlyWarningFormDto
     * @return 设备预警信息列表
     */
    public List<WcsDeviceEarlyWarningFormDto> selectWcsDeviceBaseInfoList(WcsDeviceEarlyWarningFormDto wcsDeviceEarlyWarningFormDto) {
        QueryWrapper<WcsDeviceEarlyWarningInfo> qw = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(wcsDeviceEarlyWarningFormDto)) {
            if (StrUtil.isNotEmpty(wcsDeviceEarlyWarningFormDto.getDeviceInfoId())) {
                qw.like("t.device_no", wcsDeviceEarlyWarningFormDto.getDeviceInfoId());
            }
            if (StrUtil.isNotEmpty(wcsDeviceEarlyWarningFormDto.getDeviceName())) {
                qw.like("t.device_name", wcsDeviceEarlyWarningFormDto.getDeviceName());
            }
            if (StrUtil.isNotEmpty(wcsDeviceEarlyWarningFormDto.getDeviceType())) {
                qw.eq("t.device_type", wcsDeviceEarlyWarningFormDto.getDeviceType());
            }
        }
        qw.eq("t.enable_status", DelFlagEnum.DEL_NO.getCode());
        qw.eq("t.del_flag", DelFlagEnum.DEL_NO.getCode());
        qw.orderByDesc("d.warning_time");
        return wcsDeviceEarlyWarningInfoMapper.select(qw);
    }

    /**
     * 查看预警信息详情
     * @param id  id
     * @return WcsDeviceEarlyWarningFormDto WcsDeviceEarlyWarningFormDto
     */
    public WcsDeviceEarlyWarningFormDto getInfo(String id) {
        if (StrUtil.isNotEmpty(id)) {
            return wcsDeviceEarlyWarningInfoMapper.getInfoById(id);
        } else {
            throw new ServiceException("查看详情缺失必要参数");
        }
    }
}
