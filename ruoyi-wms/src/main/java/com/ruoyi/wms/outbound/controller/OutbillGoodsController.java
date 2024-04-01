package com.ruoyi.wms.outbound.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.service.OutbillGoodsService;
import com.ruoyi.wms.outbound.dto.OutbillGoodsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 出库单货物Controller
 *
 * @author ruoyi
 * @date 2023-02-07
 */
@RestController
@RequestMapping("/outbound/outbillgoods")
public class OutbillGoodsController extends BaseController {

    @Autowired
    private OutbillGoodsService outbillGoodsService;

    /**
     * 查询出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/list");
        startPage();
        List<OutbillGoods> list = outbillGoodsService.selectOutbillGoodsList(outbillGoods);
        return getDataTable(list);
    }
    /**
     * 分组查询出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:list')")
    @PostMapping("/selectList")
    public TableDataInfo selectList(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/selectList");
        startPage();
        List<OutbillGoods> list = outbillGoodsService.selectList(outbillGoods);
        return getDataTable(list);
    }
    /**
     * 分组查询出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:list')")
    @PostMapping("/listGroup")
    public TableDataInfo listGroup(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/listGroup");
        startPage();
        List<OutbillGoods> list = outbillGoodsService.listGroup(outbillGoods);
        return getDataTable(list);
    }

    /**
     * 导出出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:export')")
    @Log(title = "出库单货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/export");
        List<OutbillGoods> list = outbillGoodsService.selectOutbillGoodsList(outbillGoods);
        ExcelUtil<OutbillGoods> util = new ExcelUtil<OutbillGoods>(OutbillGoods.class);
        util.exportExcel(response, list, "出库单货物数据");
    }

    /**
     * 导入出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:import')")
    @Log(title = "出库单货物", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/outbound/outbillgoods/import");
        ExcelUtil<OutbillGoods> util = new ExcelUtil<OutbillGoods>(OutbillGoods.class);
        List<OutbillGoods> outbillGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = outbillGoodsService.importData(outbillGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<OutbillGoods> util = new ExcelUtil<OutbillGoods>(OutbillGoods.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取出库单货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/outbound/outbillgoods/getInfo/id");
        return success(outbillGoodsService.selectOutbillGoodsById(id));
    }

    /**
     * 新增出库单货物
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:add')")
    @Log(title = "出库单货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/add");
        return AjaxResult.success(outbillGoodsService.insertOutbillGoods(outbillGoods));
    }

    /**
     * 修改出库单货物
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:edit')")
    @Log(title = "出库单货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/edit");
        return AjaxResult.success(outbillGoodsService.updateOutbillGoods(outbillGoods));
    }

    /**
     * 删除出库单货物
     */
    @PreAuthorize("@ss.hasPermi('outbound:outbillgoods:remove')")
    @Log(title = "出库单货物", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/outbound/outbillgoods/remove/id");
        return AjaxResult.success(outbillGoodsService.deleteOutbillGoodsByIds(ids));
    }

    /**
     * 查询出库详情-PDA
     */
    @PostMapping("/selectOutbillDetail")
    public TableDataInfo selectOutbillDetail(@RequestBody OutbillGoods outbillGoods) {
        logger.info("/outbound/outbillgoods/selectOutbillDetail");
        startPage();
        List<OutbillGoods> outbillGoodsList = outbillGoodsService.selectOutbillDetail(outbillGoods);
        return getDataTable(outbillGoodsList);
    }

    /**
     * 出库取盘-PDA
     */
    @PostMapping("/takeTray")
    public AjaxResult takeTray(@RequestBody OutbillGoodsDto map) {
        logger.info("/outbound/outbillgoods/takeTray");
        return outbillGoodsService.takeTray(map);
    }

    /**
     * 拣货出库扫描托盘-PDA
     */
    @PostMapping("/getTrayGoods")
    public AjaxResult getTrayGoods(@RequestBody OutbillGoods map) {
        logger.info("/outbound/outbillgoods/getTrayGoods");
        return outbillGoodsService.getTrayGoods(map);
    }

    /**
     * 拣货出库-PDA
     */
    @PostMapping("/outBill")
    public AjaxResult outBill(@RequestBody List<OutbillGoodsDto> mapList) {
        logger.info("/outbound/outbillgoods/outBill");
        return outbillGoodsService.outBill(mapList);
    }
    /**
     * PDA-拉取离线数据
     */
    @GetMapping("/pullData")
    public AjaxResult pullData() {
        logger.info("/outbound/outbillgoods/pullData");
        return outbillGoodsService.pullData();
    }

}
