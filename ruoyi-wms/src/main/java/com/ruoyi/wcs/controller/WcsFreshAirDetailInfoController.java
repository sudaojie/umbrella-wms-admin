package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wcs.domain.dto.WcsFreshAirFormDto;
import com.ruoyi.wcs.domain.dto.WcsFreshAirParamDto;
import com.ruoyi.wcs.service.WcsFreshAirDetailInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;

/**
 * 新风系统详情信息Controller
 *
 * @author hewei
 * @date 2023-04-12
 */
@RestController
@RequestMapping("/wcs/freshAirDetailInfo")
public class WcsFreshAirDetailInfoController extends BaseController {

    @Autowired
    private WcsFreshAirDetailInfoService wcsFreshAirDetailInfoService;

    /**
     * 查询WCS新风系统基本信息列表
     */
    @PostMapping("/list")
    public AjaxResult list(@RequestBody WcsFreshAirParamDto wcsFreshAirParamDto) {
        logger.info("/wcs/freshAirDetailInfo/list");
        return AjaxResult.success(wcsFreshAirDetailInfoService.queryList(wcsFreshAirParamDto));
    }

    /**
     * 保存WCS新风系统基本信息
     */
    @PostMapping("/saveData")
    public AjaxResult saveData(@RequestBody WcsFreshAirFormDto wcsFreshAirFormDto) {
        logger.info("/wcs/freshAirDetailInfo/saveData");
        return AjaxResult.success(wcsFreshAirDetailInfoService.saveData(wcsFreshAirFormDto));
    }

    /**
     * 开启WCS新风系统
     */
    @RequestMapping("/start")
    public AjaxResult start(@RequestParam String id) {
        logger.info("/wcs/freshAirDetailInfo/start");
        return wcsFreshAirDetailInfoService.start(id);
    }

    /**
     * 停止WCS新风系统
     */
    @RequestMapping("/pause")
    public AjaxResult pause(@RequestParam String id) {
        logger.info("/wcs/freshAirDetailInfo/pause");
        return AjaxResult.success(wcsFreshAirDetailInfoService.pause(id));
    }

    /**
     * 查询WCS爆闪灯系统基本信息列表
     */
    @GetMapping("/queryExplosiveFlashList")
    public AjaxResult queryExplosiveFlashList() {
        logger.info("/wcs/freshAirDetailInfo/queryExplosiveFlashList");
        return AjaxResult.success(wcsFreshAirDetailInfoService.queryExplosiveFlashList());
    }


    /**
     * 保存WCS爆闪灯系统基本信息
     */
    @PostMapping("/saveExplosiveFlash")
    public AjaxResult saveExplosiveFlash(@RequestBody WcsFreshAirFormDto wcsFreshAirFormDto) {
        logger.info("/wcs/freshAirDetailInfo/saveExplosiveFlash");
        return AjaxResult.success(wcsFreshAirDetailInfoService.saveExplosiveFlash(wcsFreshAirFormDto));
    }

}
