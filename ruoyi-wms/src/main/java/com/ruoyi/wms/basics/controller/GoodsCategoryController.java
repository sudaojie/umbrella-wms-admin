package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.GoodsCategory;
import com.ruoyi.wms.basics.service.GoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 货物类别信息Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/basics/goodscategory")
public class GoodsCategoryController extends BaseController {

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    /**
     * 查询货物类别信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody GoodsCategory goodsCategory) {
        logger.info("/basics/goodscategory/list");
        startPage();
        List<GoodsCategory> list = goodsCategoryService.selectGoodsCategoryList(goodsCategory);
        return getDataTable(list);
    }

    /**
     * 导出货物类别信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:export')")
    @Log(title = "货物类别信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GoodsCategory goodsCategory) {
        logger.info("/basics/goodscategory/export");
        List<GoodsCategory> list = goodsCategoryService.selectGoodsCategoryList(goodsCategory);
        ExcelUtil<GoodsCategory> util = new ExcelUtil<GoodsCategory>(GoodsCategory.class);
        util.exportExcel(response, list, "货物类别信息数据");
    }

    /**
     * 导入货物类别信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:import')")
    @Log(title = "货物类别信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/basics/goodscategory/import");
        ExcelUtil<GoodsCategory> util = new ExcelUtil<GoodsCategory>(GoodsCategory.class);
        List<GoodsCategory> goodsCategoryList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = goodsCategoryService.importData(goodsCategoryList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<GoodsCategory> util = new ExcelUtil<GoodsCategory>(GoodsCategory.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取货物类别信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/goodscategory/getInfo/id");
        return success(goodsCategoryService.selectGoodsCategoryById(id));
    }

    /**
     * 新增货物类别信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:add')")
    @Log(title = "货物类别信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody GoodsCategory goodsCategory) {
        logger.info("/basics/goodscategory/add");
        return AjaxResult.success(goodsCategoryService.insertGoodsCategory(goodsCategory));
    }

    /**
     * 修改货物类别信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:edit')")
    @Log(title = "货物类别信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody GoodsCategory goodsCategory) {
        if(StringUtils.isEmpty(goodsCategory.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/basics/goodscategory/edit");
        return AjaxResult.success(goodsCategoryService.updateGoodsCategory(goodsCategory));
    }

    /**
     * 删除货物类别信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:remove')")
    @Log(title = "货物类别信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/goodscategory/remove/id");
        return goodsCategoryService.deleteGoodsCategoryByIds(ids);
    }

    /**
     * 检查货物类别
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:query')")
    @Log(title = "货物类别信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody GoodsCategory goodsCategory) {
        logger.info("/basics/goodscategory/checkData");
        return goodsCategoryService.checkData(goodsCategory);
    }

    /**
     * 获取货物类别
     */
    @PreAuthorize("@ss.hasPermi('basics:goodscategory:query')")
    @Log(title = "货物类别信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getGoodscategoryData")
    public AjaxResult getGoodscategoryData() {
        logger.info("/basics/goodscategory/getGoodscategoryData");
        return AjaxResult.success(goodsCategoryService.getGoodscategoryData());
    }
}
