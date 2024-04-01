package com.ruoyi.wcs.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wcs.domain.WcsElectricalEnergyCollectInfo;
import com.ruoyi.wcs.mapper.WcsElectricalEnergyCollectInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Validator;


/**
 * wcs电能能耗信息采集Service接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Slf4j
@Service
public class WcsElectricalEnergyCollectInfoService extends ServiceImpl<WcsElectricalEnergyCollectInfoMapper, WcsElectricalEnergyCollectInfo> {

    @Autowired
    private WcsElectricalEnergyCollectInfoMapper wcsElectricalEnergyCollectInfoMapper;

    @Autowired
    protected Validator validator;

}
