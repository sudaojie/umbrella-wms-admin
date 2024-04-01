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
import com.ruoyi.wms.check.domain.CheckRealitygoods;
import com.ruoyi.wms.check.service.CheckRealitygoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点实盘货物单Controller
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@RestController
@RequestMapping("/check/realitygoods")
public class CheckRealitygoodsController extends BaseController {

    @Autowired
    private CheckRealitygoodsService checkRealitygoodsService;

    /**
     * 查询库存盘点实盘货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckRealitygoods checkRealitygoods) {
        logger.info("/check/realitygoods/list");
        startPage();
        List<CheckRealitygoods> list = checkRealitygoodsService.selectCheckRealitygoodsList(checkRealitygoods);
        return getDataTable(list);
    }

    /**
     * 导出库存盘点实盘货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:export')")
    @Log(title = "库存盘点实盘货物单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckRealitygoods checkRealitygoods){
        logger.info("/check/realitygoods/export");
        List<CheckRealitygoods> list = checkRealitygoodsService.selectCheckRealitygoodsList(checkRealitygoods);
        ExcelUtil<CheckRealitygoods> util = new ExcelUtil<CheckRealitygoods>(CheckRealitygoods.class);
        util.exportExcel(response, list, "库存盘点实盘货物单数据");
    }
    /**
     * 导入库存盘点实盘货物单列表
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:import')")
    @Log(title = "库存盘点实盘货物单", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/check/realitygoods/import");
        ExcelUtil<CheckRealitygoods> util = new ExcelUtil<CheckRealitygoods>(CheckRealitygoods.class);
        List<CheckRealitygoods> checkRealitygoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = checkRealitygoodsService.importData(checkRealitygoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckRealitygoods> util = new ExcelUtil<CheckRealitygoods>(CheckRealitygoods.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取库存盘点实盘货物单详细信息
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/check/realitygoods/getInfo/id");
        return success(checkRealitygoodsService.selectCheckRealitygoodsById(id));
    }

    /**
     * 新增库存盘点实盘货物单
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:add')")
    @Log(title = "库存盘点实盘货物单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckRealitygoods checkRealitygoods){
        logger.info("/check/realitygoods/add");
        return AjaxResult.success(checkRealitygoodsService.insertCheckRealitygoods(checkRealitygoods));
    }

    /**
     * 修改库存盘点实盘货物单
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:edit')")
    @Log(title = "库存盘点实盘货物单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckRealitygoods checkRealitygoods){
        if(StringUtils.isEmpty(checkRealitygoods.getId())){
            return AjaxResult.error("修改失败，数据主键id缺失");
        }
        logger.info("/check/realitygoods/edit");
        return AjaxResult.success(checkRealitygoodsService.updateCheckRealitygoods(checkRealitygoods));
    }

    /**
     * 删除库存盘点实盘货物单
     */
    @PreAuthorize("@ss.hasPermi('check:realitygoods:remove')")
    @Log(title = "库存盘点实盘货物单", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/check/realitygoods/remove/id");
        return AjaxResult.success(checkRealitygoodsService.deleteCheckRealitygoodsByIds(ids));
    }

    /**
     * pda盘点托盘
     * @param list
     * @return
     */
    @Log(title = "pda盘点托盘", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkTray")
    public AjaxResult checkTray(@RequestBody List<CheckRealitygoods> list){
        logger.info("/check/realitygoods/checkTray");
        checkRealitygoodsService.checkTray(list);
        return AjaxResult.success("成功");
    }

}
