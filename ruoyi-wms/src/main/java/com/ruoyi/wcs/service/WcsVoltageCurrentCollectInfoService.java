package com.ruoyi.wcs.service;

import java.util.Arrays;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.domain.WcsVoltageCurrentCollectInfo;
import com.ruoyi.wcs.mapper.WcsVoltageCurrentCollectInfoMapper;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Validator;

import com.ruoyi.common.utils.StringUtils;

/**
 * wcs电压电流信息采集Service接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Slf4j
@Service
public class WcsVoltageCurrentCollectInfoService extends ServiceImpl<WcsVoltageCurrentCollectInfoMapper, WcsVoltageCurrentCollectInfo> {

    @Autowired
    private WcsVoltageCurrentCollectInfoMapper wcsVoltageCurrentCollectInfoMapper;

    @Autowired
    protected Validator validator;

    /**
     * 查询wcs电压电流信息采集
     *
     * @param id wcs电压电流信息采集主键
     * @return wcs电压电流信息采集
     */
    public WcsVoltageCurrentCollectInfo selectWcsVoltageCurrentCollectInfoById(String id) {
        QueryWrapper<WcsVoltageCurrentCollectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wcsVoltageCurrentCollectInfoMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询wcs电压电流信息采集
     *
     * @param ids wcs电压电流信息采集 IDs
     * @return wcs电压电流信息采集
     */
    public List<WcsVoltageCurrentCollectInfo> selectWcsVoltageCurrentCollectInfoByIds(String[] ids) {
        QueryWrapper<WcsVoltageCurrentCollectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wcsVoltageCurrentCollectInfoMapper.selectList(queryWrapper);
    }

    /**
     * 查询wcs电压电流信息采集列表
     *
     * @param wcsVoltageCurrentCollectInfo wcs电压电流信息采集
     * @return wcs电压电流信息采集集合
     */
    public List<WcsVoltageCurrentCollectInfo> selectWcsVoltageCurrentCollectInfoList(WcsVoltageCurrentCollectInfo wcsVoltageCurrentCollectInfo) {
        QueryWrapper<WcsVoltageCurrentCollectInfo> queryWrapper = getQueryWrapper(wcsVoltageCurrentCollectInfo);
        return wcsVoltageCurrentCollectInfoMapper.select(queryWrapper);
    }

    /**
     * 修改wcs电压电流信息采集
     *
     * @param wcsVoltageCurrentCollectInfo wcs电压电流信息采集
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsVoltageCurrentCollectInfo updateWcsVoltageCurrentCollectInfo(WcsVoltageCurrentCollectInfo wcsVoltageCurrentCollectInfo) {
        if (StringUtils.isEmpty(wcsVoltageCurrentCollectInfo.getId())) {
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        wcsVoltageCurrentCollectInfoMapper.updateById(wcsVoltageCurrentCollectInfo);
        return wcsVoltageCurrentCollectInfo;
    }


    public QueryWrapper<WcsVoltageCurrentCollectInfo> getQueryWrapper(WcsVoltageCurrentCollectInfo wcsVoltageCurrentCollectInfo) {
        QueryWrapper<WcsVoltageCurrentCollectInfo> queryWrapper = new QueryWrapper<>();
        if (wcsVoltageCurrentCollectInfo != null) {

            //设备编号
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getDeviceInfoId())) {
                queryWrapper.eq("device_info_id", wcsVoltageCurrentCollectInfo.getDeviceInfoId());
            }
            //通讯地址
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPostalAddress())) {
                queryWrapper.eq("postal_address", wcsVoltageCurrentCollectInfo.getPostalAddress());
            }
            //A相电压(V)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseVoltageA())) {
                queryWrapper.eq("phase_voltage_a", wcsVoltageCurrentCollectInfo.getPhaseVoltageA());
            }
            //B相电压(V)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseVoltageB())) {
                queryWrapper.eq("phase_voltage_b", wcsVoltageCurrentCollectInfo.getPhaseVoltageB());
            }
            //C相电压(V)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseVoltageC())) {
                queryWrapper.eq("phase_voltage_c", wcsVoltageCurrentCollectInfo.getPhaseVoltageC());
            }
            //A相电流(A)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseCurrentA())) {
                queryWrapper.eq("phase_current_a", wcsVoltageCurrentCollectInfo.getPhaseCurrentA());
            }
            //B相电流(A)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseCurrentB())) {
                queryWrapper.eq("phase_current_b", wcsVoltageCurrentCollectInfo.getPhaseCurrentB());
            }
            //C相电流(A)
            if (StrUtil.isNotEmpty(wcsVoltageCurrentCollectInfo.getPhaseCurrentC())) {
                queryWrapper.eq("phase_current_c", wcsVoltageCurrentCollectInfo.getPhaseCurrentC());
            }
            //采集时间
            if (wcsVoltageCurrentCollectInfo.getCollectTime() != null) {
                queryWrapper.eq("collect_time", wcsVoltageCurrentCollectInfo.getCollectTime());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

}
