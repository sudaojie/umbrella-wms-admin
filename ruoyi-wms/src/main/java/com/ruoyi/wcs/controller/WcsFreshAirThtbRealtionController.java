package com.ruoyi.wcs.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wcs.domain.WcsFreshAirThtbRealtion;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.service.WcsFreshAirThtbRealtionService;
import com.ruoyi.wcs.service.WcsGateWayRealtionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WCS新风温湿度传感器关联关系Controller
 *
 * @author yangjie
 * @date 2023-03-31
 */
@RestController
@RequestMapping("/wcs/freshAirThtbRealtion")
public class WcsFreshAirThtbRealtionController extends BaseController {

    @Autowired
    private WcsFreshAirThtbRealtionService wcsFreshAirThtbRealtionService;

    @Autowired
    private WcsGateWayRealtionService wcsGateWayRealtionService;

    /**
     * 查询WCS新风温湿度传感器关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        logger.info("/wcs/freshAirThtbRealtion/list");
        startPage();
        List<WcsFreshAirThtbRealtion> list = wcsFreshAirThtbRealtionService.selectWcsFreshAirThtbRealtionList(wcsFreshAirThtbRealtion);
        return getDataTable(list);
    }

    /**
     * 导出WCS新风温湿度传感器关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:export')")
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        logger.info("/wcs/freshAirThtbRealtion/export");
        List<WcsFreshAirThtbRealtion> list = wcsFreshAirThtbRealtionService.selectWcsFreshAirThtbRealtionList(wcsFreshAirThtbRealtion);
        ExcelUtil<WcsFreshAirThtbRealtion> util = new ExcelUtil<WcsFreshAirThtbRealtion>(WcsFreshAirThtbRealtion.class);
        util.exportExcel(response, list, "WCS新风温湿度传感器关联关系数据");
    }

    /**
     * 导入WCS新风温湿度传感器关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:import')")
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/wcs/freshAirThtbRealtion/import");
        ExcelUtil<WcsFreshAirThtbRealtion> util = new ExcelUtil<WcsFreshAirThtbRealtion>(WcsFreshAirThtbRealtion.class);
        List<WcsFreshAirThtbRealtion> wcsFreshAirThtbRealtionList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wcsFreshAirThtbRealtionService.importData(wcsFreshAirThtbRealtionList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<WcsFreshAirThtbRealtion> util = new ExcelUtil<WcsFreshAirThtbRealtion>(WcsFreshAirThtbRealtion.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取WCS新风温湿度传感器关联关系详细信息
     */
    // @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wcs/freshAirThtbRealtion/getInfo/id");
        return success(wcsFreshAirThtbRealtionService.selectWcsFreshAirThtbRealtionById(id));
    }

    /**
     * 获取WCS新风温湿度传感器关联关系详细信息
     */
    // @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:query')")
    @GetMapping(value = "/queryRelationById/{id}")
    public AjaxResult queryRelationById(@PathVariable("id") String id) {
        logger.info("/wcs/freshAirThtbRealtion/queryRelationById/id");
        return success(wcsFreshAirThtbRealtionService.queryRelationById(id));
    }

    /**
     * 新增WCS新风温湿度传感器关联关系
     */
    // @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:add')")
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        logger.info("/wcs/freshAirThtbRealtion/add");
        return AjaxResult.success(wcsFreshAirThtbRealtionService.insertWcsFreshAirThtbRealtion(wcsFreshAirThtbRealtion));
    }

    /**
     * 新增WCS网关传感器关联关系
     */
    @Log(title = "新增WCS网关传感器关联关系", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addGateWayRelation")
    public AjaxResult addGateWayRelation(@RequestBody WcsGateWayRealtion wcsGateWayRealtion) {
        logger.info("/wcs/freshAirThtbRealtion/addGateWayRelation");
        return AjaxResult.success(wcsGateWayRealtionService.insertWcsGateWayRealtion(wcsGateWayRealtion));
    }

    /**
     * 修改WCS新风温湿度传感器关联关系
     */
    // @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:edit')")
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WcsFreshAirThtbRealtion wcsFreshAirThtbRealtion) {
        logger.info("/wcs/freshAirThtbRealtion/edit");
        return AjaxResult.success(wcsFreshAirThtbRealtionService.updateWcsFreshAirThtbRealtion(wcsFreshAirThtbRealtion));
    }

    /**
     * 删除WCS新风温湿度传感器关联关系
     */
    // @PreAuthorize("@ss.hasPermi('wcs:freshAirThtbRealtion:remove')")
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wcs/freshAirThtbRealtion/remove/id");
        return AjaxResult.success(wcsFreshAirThtbRealtionService.deleteWcsFreshAirThtbRealtionByIds(ids));
    }

    /**
     * 获取设备下拉列表
     */
    @Log(title = "获取设备下拉列表", businessType = BusinessType.OTHER)
    @GetMapping("/listTypeDeviceInfos")
    public AjaxResult listTypeDeviceInfos(@RequestParam(value = "deviceType") String deviceType) {
        logger.info("/wcs/freshAirThtbRealtion/listTypeDeviceInfos");
        return AjaxResult.success(wcsFreshAirThtbRealtionService.listTypeDeviceInfos(deviceType));
    }


}
