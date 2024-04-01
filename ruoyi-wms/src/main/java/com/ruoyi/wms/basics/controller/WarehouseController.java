package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 仓库基本信息Controller
 *
 * @author ruoyi
 * @date 2023-01-30
 */
@RestController
@RequestMapping("/basics/warehouse")
public class WarehouseController extends BaseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 查询仓库基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Warehouse warehouse) {
        logger.info("/basics/warehouse/list");
        startPage();
        List<Warehouse> list = warehouseService.selectWarehouseList(warehouse);
        return getDataTable(list);
    }

    /**
     * 导出仓库基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:export')")
    @Log(title = "仓库基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Warehouse warehouse) {
        logger.info("/basics/warehouse/export");
        List<Warehouse> list = warehouseService.selectWarehouseList(warehouse);
        ExcelUtil<Warehouse> util = new ExcelUtil<Warehouse>(Warehouse.class);
        util.exportExcel(response, list, "仓库基本信息数据");
    }

    /**
     * 获取仓库基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/warehouse/getInfo/id");
        return success(warehouseService.selectWarehouseById(id));
    }

    /**
     * 新增仓库基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:add')")
    @Log(title = "仓库基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Warehouse warehouse) {
        logger.info("/basics/warehouse/add");
        return AjaxResult.success(warehouseService.insertWarehouse(warehouse));
    }

    /**
     * 检查仓库信息
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:add')")
    @Log(title = "仓库基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Warehouse warehouse) {
        logger.info("/basics/warehouse/checkData");
        return warehouseService.checkData(warehouse);
    }

    /**
     * 修改仓库基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:edit')")
    @Log(title = "仓库基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Warehouse warehouse) {
        logger.info("/basics/warehouse/edit");
        return AjaxResult.success(warehouseService.updateWarehouse(warehouse));
    }

    /**
     * 删除仓库基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:warehouse:remove')")
    @Log(title = "仓库基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/warehouse/remove/id");
        return warehouseService.deleteWarehouseByIds(ids);
    }

    /**
     * 获取仓库信息
     */
    @Log(title = "获取仓库信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getWarehouseData")
    public AjaxResult getWarehouseData() {
        logger.info("/basics/area/getWarehouseData");
        return AjaxResult.success(warehouseService.getWarehouseData());
    }

    @Log(title = "仓库信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Warehouse> util = new ExcelUtil<Warehouse>(Warehouse.class);
        List<Warehouse> warehouseList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = warehouseService.importData(warehouseList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Warehouse> util = new ExcelUtil<Warehouse>(Warehouse.class);
        util.importTemplateExcel(response, "模板数据");
    }
}
