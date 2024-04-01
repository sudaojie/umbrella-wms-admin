package com.ruoyi.wcs.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WCS设备基本信息Controller
 *
 * @author yangjie
 * @date 2023-02-24
 */
@RestController
@RequestMapping("/wcs/deviceBaseInfo")
public class WcsDeviceBaseInfoController extends BaseController {

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    /**
     * 查询WCS设备基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/list");
        startPage();
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.selectWcsDeviceBaseInfoList(wcsDeviceBaseInfo);
        return getDataTable(list);
    }

    /**
     * 导出WCS设备基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:export')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/export");
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.selectWcsDeviceBaseInfoList(wcsDeviceBaseInfo);
        ExcelUtil<WcsDeviceBaseInfo> util = new ExcelUtil<WcsDeviceBaseInfo>(WcsDeviceBaseInfo.class);
        util.exportExcel(response, list, "WCS设备基本信息数据");
    }

    /**
     * 导入WCS设备基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:import')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/wcs/deviceBaseInfo/import");
        ExcelUtil<WcsDeviceBaseInfo> util = new ExcelUtil<WcsDeviceBaseInfo>(WcsDeviceBaseInfo.class);
        List<WcsDeviceBaseInfo> wcsDeviceBaseInfoList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wcsDeviceBaseInfoService.importData(wcsDeviceBaseInfoList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<WcsDeviceBaseInfo> util = new ExcelUtil<WcsDeviceBaseInfo>(WcsDeviceBaseInfo.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取WCS设备基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wcs/deviceBaseInfo/getInfo/id");
        return success(wcsDeviceBaseInfoService.selectWcsDeviceBaseInfoById(id));
    }

    /**
     * 新增WCS设备基本信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:add')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/add");
        return AjaxResult.success(wcsDeviceBaseInfoService.insertWcsDeviceBaseInfo(wcsDeviceBaseInfo));
    }

    /**
     * 修改WCS设备基本信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:edit')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/edit");
        return AjaxResult.success(wcsDeviceBaseInfoService.updateWcsDeviceBaseInfo(wcsDeviceBaseInfo));
    }

    /**
     * 修改WCS设备基本信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:editList')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/editList")
    public AjaxResult editList(@RequestBody List<WcsDeviceBaseInfo> wcsDeviceBaseInfoList) {
        logger.info("/wcs/deviceBaseInfo/editList");
        return AjaxResult.success(wcsDeviceBaseInfoService.updateWcsDeviceBaseInfoList(wcsDeviceBaseInfoList));
    }

    /**
     * 删除WCS设备基本信息
     */
    @PreAuthorize("@ss.hasPermi('wcs:deviceBaseInfo:remove')")
    @Log(title = "WCS设备基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wcs/deviceBaseInfo/remove/id");
        return AjaxResult.success(wcsDeviceBaseInfoService.deleteWcsDeviceBaseInfoByIds(ids));
    }

    /**
     * 获取新风可绑定传感器信息列表
     */
    @Log(title = "获取可绑定传感器信息列表", businessType = BusinessType.OTHER)
    @PostMapping("/getSensorInfoList")
    public AjaxResult getSensorInfoList(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/getSensorInfoList");
        return AjaxResult.success(wcsDeviceBaseInfoService.getSensorInfoList(wcsDeviceBaseInfo));
    }

    /**
     * 获取网关可绑定传设备信息列表
     */
    @Log(title = "获取网关可绑定传设备信息列表", businessType = BusinessType.OTHER)
    @PostMapping("/getGateWayDeviceInfoList")
    public AjaxResult getGateWayDeviceInfoList(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/getGateWayDeviceInfoList");
        return AjaxResult.success(wcsDeviceBaseInfoService.getGateWayDeviceInfoList(wcsDeviceBaseInfo));
    }

    /**
     * 获取电表可绑定设备信息列表
     */
    @Log(title = "获取电表可绑定设备信息列表", businessType = BusinessType.OTHER)
    @PostMapping("/getMeterDeviceInfoList")
    public AjaxResult getMeterDeviceInfoList(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/wcs/deviceBaseInfo/getMeterDeviceInfoList");
        return AjaxResult.success(wcsDeviceBaseInfoService.getMeterDeviceInfoList(wcsDeviceBaseInfo));
    }

    /**
     * 获取指定区域的摄像头列表
     */
    @Log(title = "获取指定区域的摄像头列表", businessType = BusinessType.OTHER)
    @RequestMapping("/getCameraListByType")
    public AjaxResult getCameraListByType(@RequestParam String type) {
        logger.info("/wcs/deviceBaseInfo/getCameraListByType");
        return AjaxResult.success(wcsDeviceBaseInfoService.getCameraListByType(type));
    }

    /**
     * 获取指定摄像头信息
     */
    @Log(title = "获取指定摄像头信息", businessType = BusinessType.OTHER)
    @RequestMapping("/getCameraInfoById")
    public AjaxResult getCameraInfoById(@RequestParam String id) {
        logger.info("/wcs/deviceBaseInfo/getCameraInfoById");
        return AjaxResult.success(wcsDeviceBaseInfoService.getCameraInfoById(id));
    }


    /**
     * 指定摄像头播放 可多个
     */
    @Log(title = "指定摄像头播放", businessType = BusinessType.OTHER)
    @RequestMapping("/startPlay")
    public AjaxResult startPlay(@RequestParam String ids) {
        logger.info("/wcs/deviceBaseInfo/startPlay");
        return AjaxResult.success(wcsDeviceBaseInfoService.holdStartPlayer(ids));
    }

    /**
     * 调整指定摄像头方向
     */
    @Log(title = "调整指定摄像头方向", businessType = BusinessType.OTHER)
    @RequestMapping("/adjustCameraDirection")
    public void adjustCameraDirection(@RequestParam String id, @RequestParam String command) {
        logger.info("/wcs/deviceBaseInfo/adjustCameraDirection");
        wcsDeviceBaseInfoService.adjustCameraDirection(id, command);
    }

    /**
     * 获取摄像头分组列表
     */
    @Log(title = "获取摄像头分组列表", businessType = BusinessType.OTHER)
    @RequestMapping("/getCameraListGroup")
    public AjaxResult getCameraListGroup() {
        logger.info("/wcs/deviceBaseInfo/getCameraListGroup");
        return AjaxResult.success(wcsDeviceBaseInfoService.getCameraListGroup());
    }

    /**
     * 上传摄像头截图
     */
    @Log(title = "上传摄像头截图", businessType = BusinessType.OTHER)
    @RequestMapping(value = "/uploadScreenShot")
    public void uploadScreenShot(@RequestParam MultipartFile file) throws Exception {
        logger.info("/wcs/deviceBaseInfo/uploadScreenShot");
        wcsDeviceBaseInfoService.uploadScreenShot(file);
    }

}
