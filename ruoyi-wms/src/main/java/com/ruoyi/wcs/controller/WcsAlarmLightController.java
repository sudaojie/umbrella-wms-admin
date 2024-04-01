package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wcs.service.WcsAlarmLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 爆闪灯状态变更Controller
 */
@RestController
@RequestMapping("/wcs/alarmLight")
public class WcsAlarmLightController extends BaseController {

    @Autowired
    private WcsAlarmLightService wcsAlarmLightService;

    @RequestMapping("/open")
    public AjaxResult open(){
        wcsAlarmLightService.open();
        return AjaxResult.success();
    }

    @RequestMapping("/close")
    public AjaxResult close(){
        wcsAlarmLightService.close();
        return AjaxResult.success();
    }
}
