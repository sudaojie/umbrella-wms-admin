package com.ruoyi.wms.stock.controller;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.service.DryOutbillGoodsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;


/**
 * 晾晒出库单货物Controller
 *
 * @author ruoyi
 * @date 2023-03-05
 */
@RestController
@RequestMapping("/stock/dryoutbillgoods")
public class DryOutbillGoodsController extends BaseController {

    @Autowired
    private DryOutbillGoodsService dryOutbillGoodsService;

    /**
     * 查询晾晒出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody DryOutbillGoods wmsDryOutbillGoods) {
        logger.info("/stock/dryoutbillgoods/list");
        startPage();
        List<DryOutbillGoods> list = dryOutbillGoodsService.selectWmsDryOutbillGoodsList(wmsDryOutbillGoods);
        return getDataTable(list);
    }

    /**
     * 导出晾晒出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:export')")
    @Log(title = "晾晒出库单货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DryOutbillGoods wmsDryOutbillGoods) {
        logger.info("/stock/dryoutbillgoods/export");
        List<DryOutbillGoods> list = dryOutbillGoodsService.selectWmsDryOutbillGoodsList(wmsDryOutbillGoods);
        ExcelUtil<DryOutbillGoods> util = new ExcelUtil<DryOutbillGoods>(DryOutbillGoods.class);
        util.exportExcel(response, list, "晾晒出库单货物数据");
    }

    /**
     * 导入晾晒出库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('system:goods:import')")
    @Log(title = "晾晒出库单货物", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/stock/dryoutbillgoods/import");
        ExcelUtil<DryOutbillGoods> util = new ExcelUtil<DryOutbillGoods>(DryOutbillGoods.class);
        List<DryOutbillGoods> wmsDryOutbillGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = dryOutbillGoodsService.importData(wmsDryOutbillGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<DryOutbillGoods> util = new ExcelUtil<DryOutbillGoods>(DryOutbillGoods.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取晾晒出库单货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:goods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/stock/dryoutbillgoods/getInfo/id");
        return success(dryOutbillGoodsService.selectWmsDryOutbillGoodsById(id));
    }

    /**
     * 新增晾晒出库单货物
     */
    @PreAuthorize("@ss.hasPermi('system:goods:add')")
    @Log(title = "晾晒出库单货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody DryOutbillGoods wmsDryOutbillGoods) {
        logger.info("/stock/dryoutbillgoods/add");
        return AjaxResult.success(dryOutbillGoodsService.insertWmsDryOutbillGoods(wmsDryOutbillGoods));
    }

    /**
     * 修改晾晒出库单货物
     */
    @PreAuthorize("@ss.hasPermi('system:goods:edit')")
    @Log(title = "晾晒出库单货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody DryOutbillGoods wmsDryOutbillGoods) {
        if (StringUtils.isEmpty(wmsDryOutbillGoods.getId())) {
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/stock/dryoutbillgoods/edit");
        return AjaxResult.success(dryOutbillGoodsService.updateWmsDryOutbillGoods(wmsDryOutbillGoods));
    }

    /**
     * 删除晾晒出库单货物
     */
    @PreAuthorize("@ss.hasPermi('system:goods:remove')")
    @Log(title = "晾晒出库单货物", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/stock/dryoutbillgoods/remove/id");
        return AjaxResult.success(dryOutbillGoodsService.deleteWmsDryOutbillGoodsByIds(ids));
    }

    /**
     * 开始晾晒任务
     *
     * @param map
     * @return
     */
    @Log(title = "开始晾晒任务", businessType = BusinessType.OTHER)
    @PostMapping(value = "/startDryTask")
    public AjaxResult startDryTask(@RequestBody DryOutbill map) {
        logger.info("/stock/dryoutbillgoods/startDryTask");
        return dryOutbillGoodsService.startDryTask(map);
    }

}
