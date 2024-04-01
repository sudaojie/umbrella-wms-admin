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
import com.ruoyi.wms.check.domain.CheckGoods;
import com.ruoyi.wms.check.service.CheckGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点货物单Controller
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@RestController
@RequestMapping("/check/checkGoods")
public class CheckGoodsController extends BaseController {

    @Autowired
    private CheckGoodsService checkGoodsService;

    /**
     * 查询库存盘点货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckGoods checkGoods) {
        logger.info("/check/checkGoods/list");
        startPage();
        List<CheckGoods> list = checkGoodsService.selectCheckGoodsList(checkGoods);
        return getDataTable(list);
    }

    /**
     * 导出库存盘点货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:export')")
    @Log(title = "库存盘点货物单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckGoods checkGoods){
        logger.info("/check/checkGoods/export");
        List<CheckGoods> list = checkGoodsService.selectCheckGoodsList(checkGoods);
        ExcelUtil<CheckGoods> util = new ExcelUtil<CheckGoods>(CheckGoods.class);
        util.exportExcel(response, list, "库存盘点货物单数据");
    }
    /**
     * 导入库存盘点货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:import')")
    @Log(title = "库存盘点货物单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/check/checkGoods/import");
        ExcelUtil<CheckGoods> util = new ExcelUtil<CheckGoods>(CheckGoods.class);
        List<CheckGoods> checkGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = checkGoodsService.importData(checkGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckGoods> util = new ExcelUtil<CheckGoods>(CheckGoods.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取库存盘点货物单详细信息
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/check/checkGoods/getInfo/id");
        return success(checkGoodsService.selectCheckGoodsById(id));
    }

    /**
     * 新增库存盘点货物单
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:add')")
    @Log(title = "库存盘点货物单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckGoods checkGoods){
        logger.info("/check/checkGoods/add");
        return AjaxResult.success(checkGoodsService.insertCheckGoods(checkGoods));
    }

    /**
     * 修改库存盘点货物单
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:edit')")
    @Log(title = "库存盘点货物单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckGoods checkGoods){
        if(StringUtils.isEmpty(checkGoods.getId())){
            return AjaxResult.error("修改失败，数据主键id缺失");
        }
        logger.info("/check/checkGoods/edit");
        return AjaxResult.success(checkGoodsService.updateCheckGoods(checkGoods));
    }

    /**
     * 删除库存盘点货物单
     */
    @PreAuthorize("@ss.hasPermi('check:checkGoods:remove')")
    @Log(title = "库存盘点货物单", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/check/checkGoods/remove/id");
        return AjaxResult.success(checkGoodsService.deleteCheckGoodsByIds(ids));
    }

}
