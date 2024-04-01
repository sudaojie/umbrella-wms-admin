package com.ruoyi.wms.stock.controller;

import javax.servlet.http.HttpServletResponse;

import com.beust.jcommander.Parameter;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.service.DryOutbillService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.List;


/**
 * 晾晒出库单Controller
 *
 * @author ruoyi
 * @date 2023-03-03
 */
@RestController
@RequestMapping("/stock/dryoutbill")
public class DryOutbillController extends BaseController {

    @Autowired
    private DryOutbillService dryOutbillService;

    /**
     * 查询晾晒出库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody DryOutbill wmsDryOutbill) {
        logger.info("/stock/dryoutbill/list");
        startPage();
        List<DryOutbill> list = dryOutbillService.selectWmsDryOutbillList(wmsDryOutbill);
        return getDataTable(list);
    }
    /**
     * 查询未创建入库单的晾晒出库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:list')")
    @PostMapping("/getDryOutbillList")
    public AjaxResult getDryOutbillList() {
        logger.info("/stock/dryoutbill/getDryOutbillList");
        List<DryOutbill> list = dryOutbillService.getDryOutbillList();
        return AjaxResult.success(list);
    }

    /**
     * 查询晾晒出库单详情列表
     * @param dryOutbillGoods
     * @return
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:list')")
    @PostMapping("/goodsDetailList")
    public TableDataInfo goodsDetailList(@RequestBody DryOutbillGoods dryOutbillGoods) {
        logger.info("/stock/dryoutbill/goodsDetailList");
        startPage();
        List<DryOutbill> dryOutbills = dryOutbillService.selectGoodsDetailList(dryOutbillGoods);
        return getDataTable(dryOutbills);
    }

    /**
     * 导出晾晒出库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:export')")
    @Log(title = "晾晒出库单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DryOutbill wmsDryOutbill){
        logger.info("/stock/dryoutbill/export");
        List<DryOutbill> list = dryOutbillService.selectWmsDryOutbillList(wmsDryOutbill);
        ExcelUtil<DryOutbill> util = new ExcelUtil<DryOutbill>(DryOutbill.class);
        util.exportExcel(response, list, "晾晒出库单数据");
    }
    /**
     * 导入晾晒出库单列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:import')")
    @Log(title = "晾晒出库单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception{
        logger.info("/stock/dryoutbill/import");
        ExcelUtil<DryOutbill> util = new ExcelUtil<DryOutbill>(DryOutbill.class);
        List<DryOutbill> wmsDryOutbillList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = dryOutbillService.importData(wmsDryOutbillList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<DryOutbill> util = new ExcelUtil<DryOutbill>(DryOutbill.class);
        util.importTemplateExcel(response,"模板数据");
    }

    /**
     * 获取晾晒出库单详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/stock/dryoutbill/getInfo/id");
        return success(dryOutbillService.selectWmsDryOutbillById(id));
    }

    /**
     * 查询晾晒出库单货物数据详细
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:query')")
    @PostMapping(value = "/getDryOutbillGoods")
    public TableDataInfo getDryOutbillGoods(@RequestBody DryOutbillGoods dryOutbillGoods){
        logger.info("/stock/dryoutbill/getDryOutbillGoods");
        startPage();
        List<DryOutbillGoods> dryOutbillGoodsList = dryOutbillService.getDryOutbillGoods(dryOutbillGoods);
        return getDataTable(dryOutbillGoodsList);
    }

    /**
     * 查询晾晒出库单货物数据详细
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:query')")
    @PostMapping(value = "/getDryOutbillGoodsByStatus")
    public AjaxResult getDryOutbillGoodsByStatus(@RequestBody DryOutbillGoods dryOutbillGoods){
        logger.info("/stock/dryoutbill/getDryOutbillGoodsByStatus");
        List<DryOutbillGoods> dryOutbillGoodsList = dryOutbillService.getDryOutbillGoods(dryOutbillGoods);
        return AjaxResult.success(dryOutbillGoodsList);
    }
    /**
     * 校验晾晒库区是否能容纳选择的托盘
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:query')")
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody DryOutbill wmsDryOutbill){
        logger.info("/stock/dryoutbill/checkData");
        return AjaxResult.success(dryOutbillService.checkData(wmsDryOutbill));
    }

    /**
     * 新增晾晒出库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:add')")
    @Log(title = "晾晒出库单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody DryOutbill wmsDryOutbill){
        logger.info("/stock/dryoutbill/add");
        return AjaxResult.success(dryOutbillService.insertWmsDryOutbill(wmsDryOutbill));
    }

    /**
     * 修改晾晒出库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:edit')")
    @Log(title = "晾晒出库单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody DryOutbill wmsDryOutbill){
        if(StringUtils.isEmpty(wmsDryOutbill.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/stock/dryoutbill/edit");
        return AjaxResult.success(dryOutbillService.updateWmsDryOutbill(wmsDryOutbill));
    }

    /**
     * 点击开始按钮，晾晒出库
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:edit')")
    @Log(title = "晾晒出库单", businessType = BusinessType.UPDATE)
    @PostMapping("/clickStart")
    public AjaxResult clickStart(@RequestBody DryOutbill dryOutbill){
        logger.info("/stock/dryoutbill/clickStart");
        return AjaxResult.success(dryOutbillService.clickStart(dryOutbill));
    }

    /**
     * 删除晾晒出库单
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:remove')")
    @Log(title = "晾晒出库单", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/stock/dryoutbill/remove/id");
        return AjaxResult.success(dryOutbillService.deleteWmsDryOutbillByIds(ids));
    }

    /**
     * 删除晾晒出库货物
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:remove')")
    @Log(title = "晾晒出库单", businessType = BusinessType.DELETE)
    @GetMapping("/removeDetail/{ids}")
    public AjaxResult removeDetail(@PathVariable String[] ids){
        logger.info("/stock/dryoutbill/removeDetail/id");
        return AjaxResult.success(dryOutbillService.delDryOutbillGoodsByIds(ids));
    }

    /**
     * 获取库存总览货物信息信息 (未锁定、未删除)状态
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:list')")
    @PostMapping(value = "/getGoodsList")
    public TableDataInfo getGoodsList(@RequestBody Tblstock tblstock){
        logger.info("/stock/dryoutbill/getGoodsList");
        startPage();
        List<Tblstock> list = dryOutbillService.getGoodsList(tblstock);
        return getDataTable(list);
    }

    /**
     * 初始化获取货区信息
     * @return
     */
    @PreAuthorize("@ss.hasPermi('stock:dryoutbill:list')")
    @PostMapping(value = "getAreaData")
    public AjaxResult getAreaData(){
        logger.info("/stock/dryoutbill/getAreaData");
        return  AjaxResult.success(dryOutbillService.getAreaData());
    }

}
