package com.ruoyi.wms.check.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.wms.check.domain.CheckAdjustGoods;
import com.ruoyi.wms.check.service.CheckAdjustGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点调整详情货物Controller
 *
 * @author nf
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/wms/checkAdjustGoods")
public class CheckAdjustGoodsController extends BaseController {

    @Autowired
    private CheckAdjustGoodsService checkAdjustGoodsService;

    /**
     * 查询库存盘点调整详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:list')")
    @PostMapping("/list")
    public AjaxResult list(@RequestBody CheckAdjustGoods checkAdjustGoods) {
        logger.info("/wms/checkAdjustGoods/list");
        List<CheckAdjustGoods> list = checkAdjustGoodsService.selectCheckAdjustGoodsList(checkAdjustGoods);
        return AjaxResult.success().put("rows",list);
    }

    /**
     * 导出库存盘点调整详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:export')")
    @Log(title = "库存盘点调整详情货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckAdjustGoods checkAdjustGoods){
        logger.info("/wms/checkAdjustGoods/export");
        List<CheckAdjustGoods> list = checkAdjustGoodsService.selectCheckAdjustGoodsList(checkAdjustGoods);
        ExcelUtil<CheckAdjustGoods> util = new ExcelUtil<CheckAdjustGoods>(CheckAdjustGoods.class);
        util.exportExcel(response, list, "库存盘点调整详情货物数据");
    }
    /**
     * 导入库存盘点调整详情货物列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:import')")
    @Log(title = "库存盘点调整详情货物", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/wms/checkAdjustGoods/import");
        ExcelUtil<CheckAdjustGoods> util = new ExcelUtil<CheckAdjustGoods>(CheckAdjustGoods.class);
        List<CheckAdjustGoods> checkAdjustGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = checkAdjustGoodsService.importData(checkAdjustGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckAdjustGoods> util = new ExcelUtil<CheckAdjustGoods>(CheckAdjustGoods.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取库存盘点调整详情货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/wms/checkAdjustGoods/getInfo/id");
        return success(checkAdjustGoodsService.selectCheckAdjustGoodsById(id));
    }

    /**
     * 新增库存盘点调整详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:add')")
    @Log(title = "库存盘点调整详情货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckAdjustGoods checkAdjustGoods){
        logger.info("/wms/checkAdjustGoods/add");
        return AjaxResult.success(checkAdjustGoodsService.insertCheckAdjustGoods(checkAdjustGoods));
    }

    /**
     * 修改库存盘点调整详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:edit')")
    @Log(title = "库存盘点调整详情货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckAdjustGoods checkAdjustGoods){
        logger.info("/wms/checkAdjustGoods/edit");
        return AjaxResult.success(checkAdjustGoodsService.updateCheckAdjustGoods(checkAdjustGoods));
    }

    /**
     * 删除库存盘点调整详情货物
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustGoods:remove')")
    @Log(title = "库存盘点调整详情货物", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/wms/checkAdjustGoods/remove/id");
        return AjaxResult.success(checkAdjustGoodsService.deleteCheckAdjustGoodsByIds(ids));
    }

}
