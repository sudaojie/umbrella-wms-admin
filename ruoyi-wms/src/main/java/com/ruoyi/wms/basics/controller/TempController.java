package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Temp;
import com.ruoyi.wms.basics.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 模板配置Controller
 *
 * @author ruoyi
 * @date 2023-01-09
 */
@RestController
@RequestMapping("/wms/temp")
public class TempController extends BaseController {

    @Autowired
    private TempService tempService;

    /**
     * 查询模板配置列表
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Temp wmsWarehouseTemp) {
        logger.info("/wms/temp/list");
        startPage();
        List<Temp> list = tempService.selectWmsWarehouseTempList(wmsWarehouseTemp);
        return getDataTable(list);
    }

    /**
     * 导出模板配置列表
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:export')")
    @Log(title = "模板配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Temp wmsWarehouseTemp) {
        logger.info("/wms/temp/export");
        List<Temp> list = tempService.selectWmsWarehouseTempList(wmsWarehouseTemp);
        ExcelUtil<Temp> util = new ExcelUtil<Temp>(Temp.class);
        util.exportExcel(response, list, "模板配置数据");
    }

    /**
     * 获取模板配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/wms/temp/getInfo/id");
        return success(tempService.selectWmsWarehouseTempById(id));
    }

    /**
     * 获取模板配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:query')")
    @PostMapping(value = "/checkTempId")
    public AjaxResult checkTempId(@RequestBody Temp wmsWarehouseTemp) {
        logger.info("/wms/temp/checkTempId");
        return tempService.checkTempId(wmsWarehouseTemp);
    }

    /**
     * 新增模板配置
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:add')")
    @Log(title = "模板配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Temp wmsWarehouseTemp) {
        logger.info("/wms/temp/add");
        return AjaxResult.success(tempService.insertWmsWarehouseTemp(wmsWarehouseTemp));
    }

    /**
     * 修改模板配置
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:edit')")
    @Log(title = "模板配置", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Temp wmsWarehouseTemp) {
        logger.info("/wms/temp/edit");
        return AjaxResult.success(tempService.updateWmsWarehouseTemp(wmsWarehouseTemp));
    }

    /**
     * 删除模板配置
     */
    @PreAuthorize("@ss.hasPermi('wms:temp:remove')")
    @Log(title = "模板配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/wms/temp/remove/id");
        return AjaxResult.success(tempService.deleteWmsWarehouseTempByIds(ids));
    }

    @Log(title = "模板配置", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Temp> util = new ExcelUtil<Temp>(Temp.class);
        List<Temp> tempList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = tempService.importUser(tempList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Temp> util = new ExcelUtil<Temp>(Temp.class);
        util.importTemplateExcel(response, "模板数据");
    }

}
