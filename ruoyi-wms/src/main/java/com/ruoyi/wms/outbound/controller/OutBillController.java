package com.ruoyi.wms.outbound.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.outbound.dto.OutBillPrintDto;
import com.ruoyi.wms.outbound.service.OutBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 出库单信息Controller
 *
 * @author ruoyi
 * @date 2023-02-07
 */
@RestController
@RequestMapping("/outbound/outbill")
public class OutBillController extends BaseController {

    @Autowired
    private OutBillService outBillService;

    /**
     * 查询出库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody OutBill outBill) {
        logger.info("/outbound/outbill/list");
        startPage();
        List<OutBill> list = outBillService.selectOutBillList(outBill);
        return getDataTable(list);
    }

    /**
     * 导出出库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:export')")
    @Log(title = "出库单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OutBill outBill) {
        logger.info("/outbound/outbill/export");
        List<OutBill> list = outBillService.selectOutBillList(outBill);
        ExcelUtil<OutBill> util = new ExcelUtil<OutBill>(OutBill.class);
        util.exportExcel(response, list, "出库单信息数据");
    }

    /**
     * 导入出库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:import')")
    @Log(title = "出库单信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/outbound/outbill/import");
        ExcelUtil<OutBill> util = new ExcelUtil<OutBill>(OutBill.class);
        List<OutBill> outBillList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = outBillService.importData(outBillList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<OutBill> util = new ExcelUtil<OutBill>(OutBill.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取出库单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/outbound/outbill/getInfo/id");
        return success(outBillService.selectOutBillById(id));
    }
    /**
     * 获取出库单打印信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:query')")
    @GetMapping(value = "/getPrintData/{id}")
    public AjaxResult getPrintData(@PathVariable("id") String id) {
        logger.info("/outbound/outbill/getPrintData/id");
        return success(outBillService.getPrintData(id));
    }

    /**
     * 新增出库单信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:add')")
    @Log(title = "出库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody OutBill outBill) {
        logger.info("/outbound/outbill/add");
        return AjaxResult.success(outBillService.createOutBill(outBill));
    }

    /**
     * 修改出库单信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:edit')")
    @Log(title = "出库单信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody OutBill outBill) {
        logger.info("/outbound/outbill/edit");
        return AjaxResult.success(outBillService.updateOutBill(outBill));
    }

    /**
     * 删除出库单信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:remove')")
    @Log(title = "出库单信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/outbound/outbill/remove/id");
        return AjaxResult.success(outBillService.deleteOutBillByIds(ids));
    }

    /**
     * 新增出库单信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:createOutBill')")
    @Log(title = "出库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/createOutBill")
    public AjaxResult createOutBill(@RequestBody OutBill outBill) {
        logger.info("/outbound/outbill/createOutBill");
        return AjaxResult.success(outBillService.createOutBill(outBill));
    }
    /**
     * 获取出库单打印信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbill:query')")
    @Log(title = "出库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/printData")
    public void printData(@RequestBody OutBillPrintDto map, HttpServletResponse responseBody) {
        logger.info("/outbound/outbill/printData");
        outBillService.printData(map,responseBody);
    }

}
