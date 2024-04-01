package com.ruoyi.wcs.controller;

import com.ruoyi.wcs.service.WcsFreshAirCollectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;


/**
 * 新风系统温湿度采集信息Controller
 *
 * @author ruoyi
 * @date 2023-04-12
 */
@RestController
@RequestMapping("/wcs/freshAirCollectInfo")
public class WcsFreshAirCollectInfoController extends BaseController {

    @Autowired
    private WcsFreshAirCollectInfoService wcsFreshAirCollectInfoService;

}
