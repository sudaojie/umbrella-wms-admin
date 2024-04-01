package com.ruoyi.wms.warehousing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PdfUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.dto.InBillPrintDto;
import com.ruoyi.wms.warehousing.service.InBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 入库单信息Controller
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@RestController
@RequestMapping("/warehousing/inbill")
public class InBillController extends BaseController {

    @Autowired
    private InBillService inBillService;

    /**
     * 查询入库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/list");
        startPage();
        List<InBill> list = inBillService.selectInBillList(inBill);
        return getDataTable(list);
    }

    /**
     * 导出入库单信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:export')")
    @Log(title = "入库单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InBill inBill) {
        logger.info("/warehousing/inbill/export");
        List<InBill> list = inBillService.selectInBillList(inBill);
        ExcelUtil<InBill> util = new ExcelUtil<InBill>(InBill.class);
        util.exportExcel(response, list, "入库单信息数据");
    }

    /**
     * 获取入库单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/warehousing/inbill/getInfo/id");
        return success(inBillService.selectInBillById(id));
    }

    /**
     * 新增入库单信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:add')")
    @Log(title = "入库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/add");
        return AjaxResult.success(inBillService.createInBill(inBill));
    }

    /**
     * 修改入库单信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:edit')")
    @Log(title = "入库单信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/edit")
    public AjaxResult edit(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/edit");
        return AjaxResult.success(inBillService.updateInBill(inBill));
    }
    /**
     * 修改入库单状态
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:updateStatus')")
    @Log(title = "入库单信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateStatus")
    public AjaxResult updateStatus(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/updateStatus");
        return AjaxResult.success(inBillService.updateStatus(inBill));
    }

    /**
     * 删除入库单信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:remove')")
    @Log(title = "入库单信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/warehousing/inbill/remove/id");
        return AjaxResult.success(inBillService.deleteInBillByIds(ids));
    }


    /**
     * 新增入库单
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:createInBill')")
    @Log(title = "入库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/createInBill")
    public AjaxResult createInBill(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/createInBill");
        return AjaxResult.success(inBillService.createInBill(inBill));
    }

    /**
     * 删除入库单
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:deleteInBill')")
    @Log(title = "入库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/deleteInBill")
    public AjaxResult deleteInBill(@RequestBody InBill inBill) {
        logger.info("/warehousing/inbill/deleteInBill");
        return AjaxResult.success(inBillService.deleteInBill(inBill));
    }

    /**
     * 预览入库单打印
     */
    @PreAuthorize("@ss.hasPermi('warehousing:inbill:printer')")
    @Log(title = "点击打印入库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/getPrintData")
    public AjaxResult getPrintData(@RequestBody InBill inBill) {
        return inBillService.getPrintData(inBill);
    }

    /**
     * 打印入库单，生成pdf
     */
    @Log(title = "点击打印入库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/printData")
    public void printData(@RequestBody InBillPrintDto map, HttpServletResponse responseBody) {
        inBillService.printData(map,responseBody);
    }

}
