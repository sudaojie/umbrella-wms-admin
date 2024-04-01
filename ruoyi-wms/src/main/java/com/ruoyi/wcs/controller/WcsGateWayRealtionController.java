package com.ruoyi.wcs.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wcs.domain.WcsGateWayRealtion;
import com.ruoyi.wcs.service.WcsGateWayRealtionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WCS网关设备关联关系Controller
 *
 * @author yangjie
 * @date 2023-03-31
 */
@RestController
@RequestMapping("/wcs/gateWayRealtion")
public class WcsGateWayRealtionController extends BaseController {

    @Autowired
    private WcsGateWayRealtionService wcsGateWayRealtionService;

    /**
     * 查询WCS新风温湿度传感器关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('wcs:gateWayRealtion:list')")
    @RequestMapping("/list")
    public TableDataInfo list(@RequestBody WcsGateWayRealtion wcsGateWayRealtion) {
        logger.info("/wcs/GateWayRealtion/list");
        startPage();
        List<WcsGateWayRealtion> list = wcsGateWayRealtionService.selectWcsGateWayRealtionList(wcsGateWayRealtion);
        return getDataTable(list);
    }

    /**
     * 导出WCS新风温湿度传感器关联关系列表
     */
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WcsGateWayRealtion wcsGateWayRealtion) {
        logger.info("/wcs/GateWayRealtion/export");
        List<WcsGateWayRealtion> list = wcsGateWayRealtionService.selectWcsGateWayRealtionList(wcsGateWayRealtion);
        ExcelUtil<WcsGateWayRealtion> util = new ExcelUtil<WcsGateWayRealtion>(WcsGateWayRealtion.class);
        util.exportExcel(response, list, "WCS新风温湿度传感器关联关系数据");
    }

    /**
     * 导入WCS新风温湿度传感器关联关系列表
     */
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/wcs/GateWayRealtion/import");
        ExcelUtil<WcsGateWayRealtion> util = new ExcelUtil<WcsGateWayRealtion>(WcsGateWayRealtion.class);
        List<WcsGateWayRealtion> wcsGateWayRealtionList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wcsGateWayRealtionService.importData(wcsGateWayRealtionList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<WcsGateWayRealtion> util = new ExcelUtil<WcsGateWayRealtion>(WcsGateWayRealtion.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取WCS新风温湿度传感器关联关系详细信息
     */
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wcs/GateWayRealtion/getInfo/id");
        return success(wcsGateWayRealtionService.selectWcsGateWayRealtionById(id));
    }

    /**
     * 新增WCS新风温湿度传感器关联关系
     */
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('wcs:gateWayRealtion:add')")
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WcsGateWayRealtion wcsGateWayRealtion) {
        logger.info("/wcs/GateWayRealtion/add");
        return AjaxResult.success(wcsGateWayRealtionService.insertWcsGateWayRealtion(wcsGateWayRealtion));
    }

    /**
     * 修改WCS新风温湿度传感器关联关系
     */
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('wcs:gateWayRealtion:edit')")
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WcsGateWayRealtion wcsGateWayRealtion) {
        logger.info("/wcs/GateWayRealtion/edit");
        return AjaxResult.success(wcsGateWayRealtionService.updateWcsGateWayRealtion(wcsGateWayRealtion));
    }

    /**
     * 删除WCS新风温湿度传感器关联关系
     */
    @Log(title = "WCS新风温湿度传感器关联关系", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('wcs:gateWayRealtion:remove')")
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wcs/GateWayRealtion/remove/id");
        return AjaxResult.success(wcsGateWayRealtionService.deleteWcsGateWayRealtionByIds(ids));
    }

    /**
     * 获取WCS网关采集器-设备关联关系详细信息
     */
    @GetMapping(value = "/queryRelationById/{id}")
    public AjaxResult queryRelationById(@PathVariable("id") String id) {
        logger.info("/wcs/GateWayRealtion/queryRelationById/id");
        return AjaxResult.success(wcsGateWayRealtionService.queryRelationById(id));
    }

}
