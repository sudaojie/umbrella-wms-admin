package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Supplier;
import com.ruoyi.wms.basics.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 供应商基本信息Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/basics/supplier")
public class SupplierController extends BaseController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 查询供应商基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Supplier supplier) {
        logger.info("/basics/supplier/list");
        startPage();
        List<Supplier> list = supplierService.selectSupplierList(supplier);
        return getDataTable(list);
    }

    /**
     * 导出供应商基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:export')")
    @Log(title = "供应商基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Supplier supplier) {
        logger.info("/basics/supplier/export");
        List<Supplier> list = supplierService.selectSupplierList(supplier);
        ExcelUtil<Supplier> util = new ExcelUtil<Supplier>(Supplier.class);
        util.exportExcel(response, list, "供应商基本信息数据");
    }

    /**
     * 导入供应商基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:import')")
    @Log(title = "供应商基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/basics/supplier/import");
        ExcelUtil<Supplier> util = new ExcelUtil<Supplier>(Supplier.class);
        List<Supplier> supplierList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = supplierService.importData(supplierList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Supplier> util = new ExcelUtil<Supplier>(Supplier.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取供应商基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/supplier/getInfo/id");
        return success(supplierService.selectSupplierById(id));
    }

    /**
     * 新增供应商基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:add')")
    @Log(title = "供应商基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Supplier supplier) {
        logger.info("/basics/supplier/add");
        return AjaxResult.success(supplierService.insertSupplier(supplier));
    }

    /**
     * 修改供应商基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:edit')")
    @Log(title = "供应商基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Supplier supplier) {
        logger.info("/basics/supplier/edit");
        return AjaxResult.success(supplierService.updateSupplier(supplier));
    }

    /**
     * 删除供应商基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:supplier:remove')")
    @Log(title = "供应商基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/supplier/remove/id");
        return supplierService.deleteSupplierByIds(ids);
    }

    /**
     * 检查供应商基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:query')")
    @Log(title = "供应商基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Supplier supplier) {
        logger.info("/basics/vehicle/checkData");
        return supplierService.checkData(supplier);
    }

    /**
     * 获取供应商
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:query')")
    @Log(title = "供应商基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getSupplierData")
    public AjaxResult getSupplierData() {
        logger.info("/basics/vehicle/getSupplierData");
        return AjaxResult.success(supplierService.getSupplierData());
    }
}
