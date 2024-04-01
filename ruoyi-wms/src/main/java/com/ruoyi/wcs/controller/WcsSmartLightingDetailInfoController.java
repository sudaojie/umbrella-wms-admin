package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wcs.domain.dto.WcsSmartLightParamDto;
import com.ruoyi.wcs.service.WcsSmartLightingDetailInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;

/**
 * 智慧照明系统详情信息Controller
 *
 * @author hewei
 * @date 2023-04-12
 */
@RestController
@RequestMapping("/wcs/smartLightingDetailInfo")
public class WcsSmartLightingDetailInfoController extends BaseController {

    @Autowired
    private WcsSmartLightingDetailInfoService wcsSmartLightingDetailInfoService;

    /**
     * 查询智慧照明系统基本信息列表
     */
    @PostMapping("/list")
    public AjaxResult list(@RequestBody WcsSmartLightParamDto wcsSmartLightParamDto) {
        logger.info("/wcs/smartLightingDetailInfo/list");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.queryList(wcsSmartLightParamDto));
    }

    /**
     * 保存WCS照明设备基本信息
     */
    @PostMapping("/saveData")
    public AjaxResult saveData(@RequestBody WcsSmartLightParamDto wcsSmartLightParamDto) {
        logger.info("/wcs/smartLightingDetailInfo/saveData");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.saveData(wcsSmartLightParamDto));
    }

    /**
     * 开启WCS照明设备
     */
    @RequestMapping("/start")
    public AjaxResult start(@RequestParam String id) {
        logger.info("/wcs/smartLightingDetailInfo/start");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.start(id));
    }

    /**
     * 停止WCS照明设备
     */
    @RequestMapping("/pause")
    public AjaxResult pause(@RequestParam String id) {
        logger.info("/wcs/smartLightingDetailInfo/pause");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.pause(id));
    }

    /**
     * 获取批量设置时间
     */
    @RequestMapping("/getBatchSetTime")
    public AjaxResult getBatchSetTime() {
        logger.info("/wcs/smartLightingDetailInfo/getBatchSetTime");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.getBatchSetTime());
    }

    /**
     * 批量设置时间
     */
    @RequestMapping("/saveBatchSetTime")
    public AjaxResult saveBatchSetTime(@RequestBody WcsSmartLightParamDto wcsSmartLightParamDto) throws Exception {
        logger.info("/wcs/smartLightingDetailInfo/saveBatchSetTime");
        return AjaxResult.success(wcsSmartLightingDetailInfoService.saveBatchSetTime(wcsSmartLightParamDto));
    }

}
