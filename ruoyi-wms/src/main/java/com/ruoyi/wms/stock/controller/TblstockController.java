package com.ruoyi.wms.stock.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.dto.TblstockDto;
import com.ruoyi.wms.stock.service.TblstockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 库存总览Controller
 *
 * @author ruoyi
 * @date 2023-02-06
 */
@RestController
@RequestMapping("/stock/tblstock")
public class TblstockController extends BaseController {

    @Autowired
    private TblstockService tblstockService;

    /**
     * pc查询库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:list')")
    @PostMapping("/detailList")
    public TableDataInfo detailList(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/list");
        startPage();
        List<Tblstock> list = tblstockService.selectDetailList(tblstock);
        return getDataTable(list);
    }

    /**
     * pc查询库存详情列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:query')")
    @PostMapping("/showTblstockDetail")
    public TableDataInfo showTblstockDetail(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/showTblstockDetail");
        startPage();
        List<Tblstock> list = tblstockService.showTblstockDetail(tblstock);
        return getDataTable(list);
    }

    /**
     * 查询库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/list");
        startPage();
        List<Tblstock> list = tblstockService.selectTblstockList(tblstock);
        return getDataTable(list);
    }


    /**
     * 查询可出库库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:list')")
    @PostMapping("/canOutList")
    public TableDataInfo canOutList(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/canOutList");
        List<Tblstock> list = tblstockService.canOutList(tblstock);
        return getDataTable(list);
    }


    /**
     * 导出库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:export')")
    @Log(title = "库存总览", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Tblstock tblstock) {
        logger.info("/stock/tblstock/export");
        List<Tblstock> list = tblstockService.selectTblstockList(tblstock);
        ExcelUtil<Tblstock> util = new ExcelUtil<Tblstock>(Tblstock.class);
        util.exportExcel(response, list, "库存总览数据");
    }

    /**
     * 导入库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:import')")
    @Log(title = "库存总览", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/stock/tblstock/import");
        ExcelUtil<Tblstock> util = new ExcelUtil<Tblstock>(Tblstock.class);
        List<Tblstock> tblstockList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = tblstockService.importData(tblstockList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Tblstock> util = new ExcelUtil<Tblstock>(Tblstock.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取库存总览详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/stock/tblstock/getInfo/id");
        return success(tblstockService.selectTblstockById(id));
    }

    /**
     * 新增库存总览
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:add')")
    @Log(title = "库存总览", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/add");
        return AjaxResult.success(tblstockService.insertTblstock(tblstock));
    }

    /**
     * 修改库存总览
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:edit')")
    @Log(title = "库存总览", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Tblstock tblstock) {
        logger.info("/stock/tblstock/edit");
        return AjaxResult.success(tblstockService.updateTblstock(tblstock));
    }

    /**
     * 删除库存总览
     */
    @PreAuthorize("@ss.hasPermi('stock:tblstock:remove')")
    @Log(title = "库存总览", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/stock/tblstock/remove/id");
        return AjaxResult.success(tblstockService.deleteTblstockByIds(ids));
    }

    /**
     * 查询库存中托盘分组
     * @param map
     * @return
     */
    @PostMapping("/selectTblstockTray")
    public TableDataInfo selectTblstockTray(@RequestBody TblstockDto map) {
        logger.info("/stock/tblstock/selectTblstockTray");
        startPage();
        List<Tblstock> list = tblstockService.selectTblstockTray(map);
        return getDataTable(list);
    }

    /**
     * 查询库存中托盘详情
     * @param map
     * @return
     */
    @PostMapping("/selectTblstockTrayDetail")
    public AjaxResult selectTblstockTrayDetail(@RequestBody TblstockDto map) {
        logger.info("/stock/tblstock/selectTblstockTrayDetail");
        return tblstockService.selectTblstockTrayDetail(map);
    }

}

