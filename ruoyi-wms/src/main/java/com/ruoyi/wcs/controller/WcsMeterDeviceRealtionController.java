package com.ruoyi.wcs.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.wcs.domain.WcsMeterDeviceRealtion;
import com.ruoyi.wcs.service.WcsMeterDeviceRealtionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;

import java.util.List;


/**
 * WCS电表设备关联关系Controller
 *
 * @author ruoyi
 * @date 2023-05-10
 */
@RestController
@RequestMapping("/wcs/meterDeviceRealtion")
public class WcsMeterDeviceRealtionController extends BaseController {

    @Autowired
    private WcsMeterDeviceRealtionService wcsMeterDeviceRealtionService;

    /**
     * 查询WCS电表设备关联关系列表
     */
    @RequestMapping("/list")
    public TableDataInfo list(@RequestBody WcsMeterDeviceRealtion wcsMeterDeviceRealtion) {
        logger.info("/wcs/GateWayRealtion/list");
        startPage();
        List<WcsMeterDeviceRealtion> list = wcsMeterDeviceRealtionService.selectWcsMeterGateWayRealtionList(wcsMeterDeviceRealtion);
        return getDataTable(list);
    }

    /**
     * 新增电表和各设备关联关系
     */
    @Log(title = "新增电表和各设备关联关系", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WcsMeterDeviceRealtion wcsMeterDeviceRealtion) {
        logger.info("/wcs/meterDeviceRealtion/add");
        return AjaxResult.success(wcsMeterDeviceRealtionService.insertWcsFreshAirThtbRealtion(wcsMeterDeviceRealtion));
    }

    /**
     * 获取WCS电表和设备关联关系详细信息
     */
    @GetMapping(value = "/queryRelationById/{id}")
    public AjaxResult queryRelationById(@PathVariable("id") String id) {
        logger.info("/wcs/meterDeviceRealtion/queryRelationById/id");
        return success(wcsMeterDeviceRealtionService.queryRelationById(id));
    }

    /**
     * 获取设备下拉列表
     */
    @Log(title = "获取设备下拉列表", businessType = BusinessType.OTHER)
    @GetMapping("/listTypeDeviceInfos")
    public AjaxResult listTypeDeviceInfos(@RequestParam(value = "deviceType") String deviceType) {
        logger.info("/wcs/meterDeviceRealtion/listTypeDeviceInfos");
        return AjaxResult.success(wcsMeterDeviceRealtionService.listTypeDeviceInfos(deviceType));
    }

}
