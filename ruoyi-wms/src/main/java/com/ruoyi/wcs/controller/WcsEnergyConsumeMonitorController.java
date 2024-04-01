package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.wcs.domain.dto.WcsParamDto;
import com.ruoyi.wcs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description 能耗监控
 * @author hewei
 * @date 2023/4/10 0010 13:33
 */
@RestController
@RequestMapping("/wcs/energyConsumeMonitor")
public class WcsEnergyConsumeMonitorController extends BaseController {

    @Autowired
    private WcsEnergyConsumeMonitorService wcsEnergyConsumeMonitorService;

    /**
     * 构造左侧设备树
     */
    @RequestMapping("/listSideDeviceInfoTree")
    public AjaxResult listSideDeviceInfoTree() {
        logger.info("/wcs/energyConsumeMonitor/listSideDeviceInfoTree");
        return AjaxResult.success(wcsEnergyConsumeMonitorService.listSideDeviceInfoTree());
    }

    /**
     * 加载历史记录
     */
    @GetMapping("/loadHistoryRecords")
    public TableDataInfo loadHistoryRecords(WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadHistoryRecords");
        return getDataTable(wcsEnergyConsumeMonitorService.loadHistoryRecords(wcsParamDto));
    }

    /**
     * 加载能耗占比头部数据
     */
    @RequestMapping("/loadEnergyHeaderData")
    public AjaxResult loadEnergyHeaderData(@RequestBody WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadEnergyHeaderData");
        return AjaxResult.success(wcsEnergyConsumeMonitorService.loadEnergyHeaderData(wcsParamDto));
    }

    /**
     * 加载能耗图表
     */
    @RequestMapping("/loadEnergyChartData")
    public AjaxResult loadEnergyChartData(@RequestBody WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadEnergyChartData");
        return AjaxResult.success(wcsEnergyConsumeMonitorService.loadEnergyChartData(wcsParamDto));
    }

    /**
     * 加载电压电流历史记录
     */
    @GetMapping("/loadVoltageCurrentRecords")
    public TableDataInfo loadVoltageCurrentRecords(WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadVoltageCurrentRecords");
        return getDataTable(wcsEnergyConsumeMonitorService.loadVoltageCurrentRecords(wcsParamDto));
    }

    /**
     * 加载功率历史记录
     */
    @GetMapping("/loadPowerRecords")
    public TableDataInfo loadPowerRecords(WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadPowerRecords");
        return getDataTable(wcsEnergyConsumeMonitorService.loadPowerRecords(wcsParamDto));
    }

    /**
     * 加载电压电流图表
     */
    @RequestMapping("/loadVoltageCurrentChartData")
    public AjaxResult loadVoltageCurrentChartData(@RequestBody WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadVoltageCurrentChartData");
        return AjaxResult.success(wcsEnergyConsumeMonitorService.loadVoltageCurrentChartData(wcsParamDto));
    }

    /**
     * 加载功耗图表
     */
    @RequestMapping("/loadPowerChartData")
    public AjaxResult loadPowerChartData(@RequestBody WcsParamDto wcsParamDto) {
        logger.info("/wcs/energyConsumeMonitor/loadPowerChartData");
        return AjaxResult.success(wcsEnergyConsumeMonitorService.loadPowerChartData(wcsParamDto));
    }


}
