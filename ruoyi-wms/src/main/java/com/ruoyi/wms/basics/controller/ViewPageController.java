package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.dto.UpdateTrayInfoDto;
import com.ruoyi.wms.basics.service.ViewPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2023-03-16
 */
@Anonymous
@RestController
@RequestMapping("/basics/viewController")
public class ViewPageController extends BaseController {

    @Autowired
    private ViewPageService viewPageService;

    /**
     * 根据层号获取存储区货物
     * @param location
     * @return
     */
    @PreAuthorize("@ss.hasPermi('basics:viewController:list')")
    @PostMapping("/selectBylayerList")
    public AjaxResult selectBylayerList(@RequestBody Location location) {
        logger.info("/basics/viewController/selectBylayerList");
        return AjaxResult.success(viewPageService.selectBylayerList(location));
    }

    /**
     * 根据货位号获取货物信息
     * @param location
     * @return
     */
    @PreAuthorize("@ss.hasPermi('basics:viewController:list')")
    @PostMapping("/selectGoodsByLocCodeList")
    public AjaxResult selectByGodosList(@RequestBody Location location) {
        logger.info("/basics/viewController/selectGoodsByLocCodeList");
        return AjaxResult.success(viewPageService.selectGoodsByLocCodeList(location));
    }


    /**
     * 根据类型和内容进行筛选定位库位信息
     * @param searchInput   搜索内容
     * @param searchType    搜索类型(1.库位号  2.托盘号  3.机件号)
     * @return
     */
    @PreAuthorize("@ss.hasPermi('basics:viewController:list')")
    @PostMapping("/searchLocationByParams")
    public AjaxResult searchLocationByParams(String searchInput,String searchType) {
        logger.info("/basics/viewController/searchLocationByParams");
        return AjaxResult.success(viewPageService.searchLocationByParams(searchInput,searchType));
    }



    /**
     * 获取理货区/晾晒区货物
     * @return
     */
    @PreAuthorize("@ss.hasPermi('basics:viewController:list')")
    @PostMapping("/selectLhList")
    public AjaxResult selectLhList() {
        logger.info("/basics/viewController/selectLhList");
        return AjaxResult.success(viewPageService.selectLhList());
    }

    /**
     * 获取存储区货物
     * @param location
     * @return
     */
    @PostMapping("/selectAllLocationList")
    public AjaxResult selectAllLocationList(@RequestBody Location location) {
        logger.info("/basics/viewController/selectAllLocationList");
        return AjaxResult.success(viewPageService.selectAllLocationList(location));
    }

    /**
     * 查看库位情况
     * @return
     */
    @GetMapping("/selectLocationInfo")
    public AjaxResult selectLocationInfo() {
        logger.info("/basics/viewController/selectLocationInfo");
        return AjaxResult.success(viewPageService.selectLocationInfo());
    }

    /**
     * 查看今日库存数量信息
     * @return
     */
    @GetMapping("/selectTblstockCount")
    public AjaxResult selectTblstockCount() {
        logger.info("/basics/viewController/selectTblstockCount");
        return AjaxResult.success(viewPageService.selectTblstockCount());
    }

    /**
     * 库存周转率
     * @return
     */
    @GetMapping("/getInventoryTurnover")
    public AjaxResult getInventoryTurnover() {
        logger.info("/basics/viewController/getInventoryTurnover");
        return AjaxResult.success(viewPageService.getInventoryTurnover());
    }

    /**
     * 出入库统计
     * @return
     */
    @GetMapping("/getInboundAndOutboundStatistics")
    public AjaxResult getInboundAndOutboundStatistics(@RequestParam String dateType) {
        logger.info("/basics/viewController/getInboundAndOutboundStatistics");
        return AjaxResult.success(viewPageService.getInboundAndOutboundStatistics(dateType));
    }

    /**
     * 预警信息列表
     * @return
     */
    @GetMapping("/getWarningList")
    public AjaxResult getWarningList() {
        logger.info("/basics/viewController/getWarningList");
        return AjaxResult.success(viewPageService.getWarningList());
    }

    /**
     * 任务列表
     * @return
     */
    @GetMapping("/getTaskList")
    public AjaxResult getTaskList() {
        logger.info("/basics/viewController/getTaskList");
        return AjaxResult.success(viewPageService.getTaskList());
    }

    /**
     * 设备情况
     * @return
     */
    @GetMapping("/getDeviceStatus")
    public AjaxResult getDeviceStatus() {
        logger.info("/basics/viewController/getDeviceStatus");
        return AjaxResult.success(viewPageService.getDeviceStatus());
    }

    /**
     * 用电量统计
     * @return
     */
    @GetMapping("/electricityConsumptionStatistics")
    public AjaxResult electricityConsumptionStatistics(String dateType) {
        logger.info("/basics/viewController/electricityConsumptionStatistics");
        return AjaxResult.success(viewPageService.electricityConsumptionStatistics(dateType));
    }

    /**
     * 能耗排名
     * @return
     */
    @GetMapping("/energyConsumptionRanking")
    public AjaxResult energyConsumptionRanking(String dateType) {
        logger.info("/basics/viewController/energyConsumptionRanking");
        return AjaxResult.success(viewPageService.energyConsumptionRanking(dateType));
    }

    /**
     * 能耗分析
     * @return
     */
    @GetMapping("/energyConsumptionAnalysis")
    public AjaxResult energyConsumptionAnalysis(String dateType) {
        logger.info("/basics/viewController/energyConsumptionAnalysis");
        return AjaxResult.success(viewPageService.energyConsumptionAnalysis(dateType));
    }

    /**
     * 温湿度、烟感信息
     * @return
     */
    @GetMapping("/getInstrumentInformation")
    public AjaxResult getInstrumentInformation() {
        logger.info("/basics/viewController/getInstrumentInformation");
        return AjaxResult.success(viewPageService.getInstrumentInformation());
    }

    /**
     * 获取温湿度、烟感信息
     * @return
     */
    @GetMapping("/getInstrumentInformationByDeviceNo")
    public AjaxResult getInstrumentInformationByDeviceNo(@RequestParam String deviceNo) {
        logger.info("/basics/viewController/getInstrumentInformationByDeviceNo");
        return AjaxResult.success(viewPageService.getInstrumentInformationByDeviceNo(deviceNo));
    }

    /**
     * 生成温湿度、烟感信息
     * @return
     */
    @GetMapping("/generateSmokeAndHumidity")
    public void generateSmokeAndHumidity() {
        logger.info("/basics/viewController/generateSmokeAndHumidity");
        viewPageService.generateSmokeAndHumidity();
    }



    /**
     * 修改库位托盘信息
     * @return
     */
    @PostMapping("/updateTrayInfo")
    public AjaxResult updateTrayInfo(@RequestBody UpdateTrayInfoDto updateTrayInfoDto) {
        logger.info("/basics/viewController/updateTrayInfo");
        return AjaxResult.success(viewPageService.updateTrayInfo(updateTrayInfoDto));
    }

    /**
     * 选中有托盘无货库位取盘
     * @return
     */
    @PostMapping("/locationRetrieval")
    public AjaxResult locationRetrieval(@RequestBody Location Location) {
        logger.info("/basics/viewController/locationRetrieval");
        return AjaxResult.success(viewPageService.locationRetrieval(Location));
    }

    /**
     * 禁用库位
     * @param Location
     * @return
     */
    @PostMapping("/upadateLocationEnable")
    public AjaxResult upadateLocationEnable(@RequestBody Location Location) {
        logger.info("/basics/viewController/upadateLocationEnable");
        return viewPageService.upadateLocationEnable(Location);
    }

}
