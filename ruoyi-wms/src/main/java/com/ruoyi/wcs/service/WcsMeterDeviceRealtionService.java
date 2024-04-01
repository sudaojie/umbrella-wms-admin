package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.domain.WcsMeterDeviceRealtion;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsMeterDeviceRealtionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * WCS电表设备关联关系Service接口
 *
 * @author ruoyi
 * @date 2023-05-10
 */
@Slf4j
@Service
public class WcsMeterDeviceRealtionService extends ServiceImpl<WcsMeterDeviceRealtionMapper, WcsMeterDeviceRealtion> {

    @Autowired
    private WcsMeterDeviceRealtionMapper wcsMeterDeviceRealtionMapper;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    /**
     * 新增电表和各设备关联关系
     */
    public WcsMeterDeviceRealtion insertWcsFreshAirThtbRealtion(WcsMeterDeviceRealtion wcsMeterDeviceRealtion) {
        if (StrUtil.isNotEmpty(wcsMeterDeviceRealtion.getMeterDeviceNo())) {
            if (StrUtil.isNotEmpty(wcsMeterDeviceRealtion.getOldDeviceNo())) {
                wcsMeterDeviceRealtionMapper.delete(new QueryWrapper<WcsMeterDeviceRealtion>()
                        .eq("meter_device_no", wcsMeterDeviceRealtion.getOldDeviceNo()));
            }

            List<String> thtbDeviceNos = new ArrayList<>();
            List<WcsMeterDeviceRealtion> list = new ArrayList<>();

            if (StrUtil.isNotEmpty(wcsMeterDeviceRealtion.getDeviceNo())) {
                thtbDeviceNos = Arrays.asList(wcsMeterDeviceRealtion.getDeviceNo().split(","));
            }

            if (CollUtil.isNotEmpty(thtbDeviceNos)) {
                thtbDeviceNos.forEach(item -> {
                    WcsMeterDeviceRealtion relation = new WcsMeterDeviceRealtion();
                    relation.setMeterDeviceNo(wcsMeterDeviceRealtion.getMeterDeviceNo());
                    relation.setDeviceNo(item);
                    relation.setCreateBy(SecurityUtils.getUsername());
                    relation.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    relation.setRemark(wcsMeterDeviceRealtion.getRemark());
                    list.add(relation);
                });
            }
            if (CollUtil.isNotEmpty(list)) {
                this.saveOrUpdateBatch(list);
            }
        } else {
            throw new ServiceException("未选择新风设备");
        }
        return wcsMeterDeviceRealtion;
    }

    /**
     * 回显关联关系
     * @param id id
     * @return map map
     */
    public Map<String, Object> queryRelationById(String id) {
        Map<String, Object> result = new HashMap<>();
        List<WcsDeviceBaseInfo> deviceBaseInfos = new ArrayList<>();
        if (StrUtil.isNotEmpty(id)) {
            List<WcsMeterDeviceRealtion> list = this.getBaseMapper().selectList(new QueryWrapper<WcsMeterDeviceRealtion>().
                    eq("meter_device_no", id).eq("del_flag", DelFlagEnum.DEL_NO.getCode()));
            result.put("relationList", list);
            List<String> ids = list.stream().map(WcsMeterDeviceRealtion::getDeviceNo).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(ids)) {
                deviceBaseInfos = wcsDeviceBaseInfoService.getBaseMapper().selectList(new QueryWrapper<WcsDeviceBaseInfo>().in("device_no", ids));
            }
            result.put("deviceBaseInfos", deviceBaseInfos);
        } else {
            throw new ServiceException("缺失必要参数");
        }
        return result;
    }

    /**
     * 获取设备下拉列表
     */
    public List<WcsDeviceBaseInfo> listTypeDeviceInfos(String deviceType) {
        QueryWrapper<WcsDeviceBaseInfo> qw = new QueryWrapper<>();
        qw.eq("device_type", deviceType);
        qw.eq("enable_status", DelFlagEnum.DEL_NO.getCode());
        return wcsDeviceBaseInfoService.getBaseMapper().selectList(qw);
    }

    /**
     * 查询WCS电表设备关联关系列表
     */
    public List<WcsMeterDeviceRealtion> selectWcsMeterGateWayRealtionList(WcsMeterDeviceRealtion wcsMeterDeviceRealtion) {
        QueryWrapper<WcsMeterDeviceRealtion> queryWrapper = getQueryWrapper(wcsMeterDeviceRealtion);
        return wcsMeterDeviceRealtionMapper.select(queryWrapper);
    }

    public QueryWrapper<WcsMeterDeviceRealtion> getQueryWrapper(WcsMeterDeviceRealtion wcsMeterDeviceRealtion) {
        QueryWrapper<WcsMeterDeviceRealtion> queryWrapper = new QueryWrapper<>();
        if (wcsMeterDeviceRealtion != null) {
            wcsMeterDeviceRealtion.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("wgwr.del_flag", wcsMeterDeviceRealtion.getDelFlag());
            //电表设备编号
            if (StrUtil.isNotEmpty(wcsMeterDeviceRealtion.getMeterDeviceNo())) {
                queryWrapper.eq("wgwr.meter_device_no", wcsMeterDeviceRealtion.getMeterDeviceNo());
            }
            //设备编号
            if (StrUtil.isNotEmpty(wcsMeterDeviceRealtion.getDeviceNo())) {
                queryWrapper.eq("wgwr.device_no", wcsMeterDeviceRealtion.getDeviceNo());
            }
        }
        queryWrapper.orderByDesc("wgwr.create_time");
        return queryWrapper;
    }
}
