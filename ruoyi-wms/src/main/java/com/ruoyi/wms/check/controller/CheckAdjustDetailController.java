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
import com.ruoyi.wms.check.domain.CheckAdjustDetail;
import com.ruoyi.wms.check.service.CheckAdjustDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点调整单详情Controller
 *
 * @author nf
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/wms/checkAdjustDetail")
public class CheckAdjustDetailController extends BaseController {

    @Autowired
    private CheckAdjustDetailService checkAdjustDetailService;

    /**
     * 查询库存盘点调整单详情列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckAdjustDetail checkAdjustDetail) {
        logger.info("/wms/checkAdjustDetail/list");
        startPage();
        List<CheckAdjustDetail> list = checkAdjustDetailService.selectCheckAdjustDetailList(checkAdjustDetail);
        return getDataTable(list);
    }

    /**
     * 导出库存盘点调整单详情列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:export')")
    @Log(title = "库存盘点调整单详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckAdjustDetail checkAdjustDetail){
        logger.info("/wms/checkAdjustDetail/export");
        List<CheckAdjustDetail> list = checkAdjustDetailService.selectCheckAdjustDetailList(checkAdjustDetail);
        ExcelUtil<CheckAdjustDetail> util = new ExcelUtil<CheckAdjustDetail>(CheckAdjustDetail.class);
        util.exportExcel(response, list, "库存盘点调整单详情数据");
    }
    /**
     * 导入库存盘点调整单详情列表
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:import')")
    @Log(title = "库存盘点调整单详情", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/wms/checkAdjustDetail/import");
        ExcelUtil<CheckAdjustDetail> util = new ExcelUtil<CheckAdjustDetail>(CheckAdjustDetail.class);
        List<CheckAdjustDetail> checkAdjustDetailList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = checkAdjustDetailService.importData(checkAdjustDetailList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckAdjustDetail> util = new ExcelUtil<CheckAdjustDetail>(CheckAdjustDetail.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取库存盘点调整单详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/wms/checkAdjustDetail/getInfo/id");
        return success(checkAdjustDetailService.selectCheckAdjustDetailById(id));
    }

    /**
     * 新增库存盘点调整单详情
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:add')")
    @Log(title = "库存盘点调整单详情", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckAdjustDetail checkAdjustDetail){
        logger.info("/wms/checkAdjustDetail/add");
        return AjaxResult.success(checkAdjustDetailService.insertCheckAdjustDetail(checkAdjustDetail));
    }

    /**
     * 修改库存盘点调整单详情
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:edit')")
    @Log(title = "库存盘点调整单详情", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckAdjustDetail checkAdjustDetail){
        logger.info("/wms/checkAdjustDetail/edit");
        return AjaxResult.success(checkAdjustDetailService.updateCheckAdjustDetail(checkAdjustDetail));
    }

    /**
     * 删除库存盘点调整单详情
     */
    @PreAuthorize("@ss.hasPermi('wms:checkAdjustDetail:remove')")
    @Log(title = "库存盘点调整单详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/wms/checkAdjustDetail/remove/id");
        return AjaxResult.success(checkAdjustDetailService.deleteCheckAdjustDetailByIds(ids));
    }

}
