package com.ruoyi.wcs.service;

import com.ruoyi.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WcsAlarmLightService {


    @Value("${alarm.light.remote}")
    private String alarmLightRemoteUrl;

    public void open(){
        HttpUtils.sendGet(alarmLightRemoteUrl+"/openAlarm");
    }


    public void close(){
        HttpUtils.sendGet(alarmLightRemoteUrl+"/closeAlarm");
    }
}
