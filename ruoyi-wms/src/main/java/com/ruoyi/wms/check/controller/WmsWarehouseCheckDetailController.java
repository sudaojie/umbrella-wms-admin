package com.ruoyi.wms.check.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.check.domain.Check;
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
import com.ruoyi.wms.check.domain.CheckDetail;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 库存盘点详情Controller
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@RestController
@RequestMapping("/check/checkDetail")
public class WmsWarehouseCheckDetailController extends BaseController {

    @Autowired
    private WmsWarehouseCheckDetailService wmsWarehouseCheckDetailService;

    /**
     * 查询库存盘点详情列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckDetail wmsWarehouseCheckDetail) {
        logger.info("/check/checkDetail/list");
        startPage();
        List<CheckDetail> list = wmsWarehouseCheckDetailService.selectWmsWarehouseCheckDetailList(wmsWarehouseCheckDetail);
        return getDataTable(list);
    }

    /**
     * 导出库存盘点详情列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:export')")
    @Log(title = "库存盘点详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckDetail wmsWarehouseCheckDetail){
        logger.info("/check/checkDetail/export");
        List<CheckDetail> list = wmsWarehouseCheckDetailService.selectWmsWarehouseCheckDetailList(wmsWarehouseCheckDetail);
        ExcelUtil<CheckDetail> util = new ExcelUtil<CheckDetail>(CheckDetail.class);
        util.exportExcel(response, list, "库存盘点详情数据");
    }
    /**
     * 导入库存盘点详情列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:import')")
    @Log(title = "库存盘点详情", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/check/checkDetail/import");
        ExcelUtil<CheckDetail> util = new ExcelUtil<CheckDetail>(CheckDetail.class);
        List<CheckDetail> wmsWarehouseCheckDetailList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsWarehouseCheckDetailService.importData(wmsWarehouseCheckDetailList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckDetail> util = new ExcelUtil<CheckDetail>(CheckDetail.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取库存盘点详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/check/checkDetail/getInfo/id");
        return success(wmsWarehouseCheckDetailService.selectWmsWarehouseCheckDetailById(id));
    }

    /**
     * 新增库存盘点详情
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:add')")
    @Log(title = "库存盘点详情", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckDetail wmsWarehouseCheckDetail){
        logger.info("/check/checkDetail/add");
        return AjaxResult.success(wmsWarehouseCheckDetailService.insertWmsWarehouseCheckDetail(wmsWarehouseCheckDetail));
    }

    /**
     * 修改库存盘点详情
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:edit')")
    @Log(title = "库存盘点详情", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckDetail wmsWarehouseCheckDetail){
        if(StringUtils.isEmpty(wmsWarehouseCheckDetail.getId())){
            return AjaxResult.error("修改失败，数据主键id缺失");
        }
        logger.info("/check/checkDetail/edit");
        return AjaxResult.success(wmsWarehouseCheckDetailService.updateWmsWarehouseCheckDetail(wmsWarehouseCheckDetail));
    }

    /**
     * 删除库存盘点详情
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:remove')")
    @Log(title = "库存盘点详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/check/checkDetail/remove/id");
        return AjaxResult.success(wmsWarehouseCheckDetailService.deleteWmsWarehouseCheckDetailByIds(ids));
    }

    /**
     * 盘点任务开始
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:edit')")
    @Log(title = "盘点任务开始", businessType = BusinessType.OTHER)
    @PostMapping(value = "/startCheck")
    public AjaxResult startCheck(@RequestBody Check check){
        logger.info("/check/checkDetail/startCheck");
        wmsWarehouseCheckDetailService.startCheck(check);
        return AjaxResult.success("成功");
    }
    /**
     * 盘点任务结束
     */
    @PreAuthorize("@ss.hasPermi('check:checkDetail:edit')")
    @Log(title = "盘点任务结束", businessType = BusinessType.OTHER)
    @PostMapping(value = "/endCheck")
    public AjaxResult endCheck(@RequestBody Check check){
        logger.info("/check/checkDetail/endCheck");
        return wmsWarehouseCheckDetailService.endCheck(check);
    }

}
