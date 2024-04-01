package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wcs.service.WcsAssistantDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 辅助决策Controller
 *
 * @author hewei
 * @date 2023-05-08
 */
@RestController
@RequestMapping("/wcs/assistant/decision")
public class WcsAssistantDecisionController {

    @Autowired
    private WcsAssistantDecisionService wcsAssistantDecisionService;

    /**
     * 温湿度监测
     */
    @RequestMapping("/temperatureHumidityMonitor")
    public AjaxResult temperatureHumidityMonitor(@RequestParam(value = "deviceArea", required = false) String deviceArea) {
        return AjaxResult.success(wcsAssistantDecisionService.temperatureHumidityMonitor(deviceArea));
    }

    /**
     * 碳排量监测
     */
    @RequestMapping("/carbonEmissionMonitor")
    public AjaxResult carbonEmissionMonitor(@RequestParam String type) {
        return AjaxResult.success(wcsAssistantDecisionService.carbonEmissionMonitor(type));
    }

    /**
     * 温湿度与晾晒时长分析
     */
    @RequestMapping("/analysisOfTemperatureHumidityDryingTime")
    public AjaxResult analysisOfTemperatureHumidityDryingTime() {
        return AjaxResult.success(wcsAssistantDecisionService.analysisOfTemperatureHumidityDryingTime());
    }

    /**
     * 能耗与碳排放量分析
     */
    @RequestMapping("/analysisOfEnergyConsumptionAndCarbonEmissions")
    public AjaxResult analysisOfEnergyConsumptionAndCarbonEmissions(@RequestParam(value = "deviceArea", required = false) String deviceArea,
                                                                    @RequestParam(value = "dateType", required = false) String dateType) {
        return AjaxResult.success(wcsAssistantDecisionService.analysisOfEnergyConsumptionAndCarbonEmissions(deviceArea, dateType));
    }

    /**
     * 温湿度监测列表
     */
    @RequestMapping("/temperatureAndHumidityMonitorList")
    public AjaxResult temperatureAndHumidityMonitorList(@RequestParam(value = "deviceArea", required = false) String deviceArea) {
        return AjaxResult.success(wcsAssistantDecisionService.temperatureAndHumidityMonitorList(deviceArea));
    }

    /**
     * 设备监控
     */
    @RequestMapping("/deviceMonitor")
    public AjaxResult deviceMonitor() {
        return AjaxResult.success(wcsAssistantDecisionService.deviceMonitor());
    }

}
