package com.ruoyi.wcs.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wcs.domain.WcsPowerCollectInfo;
import com.ruoyi.wcs.mapper.WcsPowerCollectInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Validator;


/**
 * wcs电压电流信息采集Service接口
 *
 * @author hewei
 * @date 2023-04-10
 */
@Slf4j
@Service
public class WcsPowerCollectInfoService extends ServiceImpl<WcsPowerCollectInfoMapper, WcsPowerCollectInfo> {

    @Autowired
    private WcsPowerCollectInfoMapper wcsPowerCollectInfoMapper;

    @Autowired
    protected Validator validator;

}
