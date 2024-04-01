package com.ruoyi.web.controller.system;

import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysFileTemp;
import com.ruoyi.system.service.ISysFileTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 系统临时附件Controller
 *
 * @author yangjie
 * @date 2022-10-28
 */
@RestController
@RequestMapping("/system/temp")
public class SysFileTempController extends BaseController {

    @Autowired
    private ISysFileTempService sysFileTempService;

    /**
     * 查询系统临时附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:temp:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFileTemp sysFileTemp) {
        startPage();
        List<SysFileTemp> list = sysFileTempService.selectSysFileTempList(sysFileTemp);
        return getDataTable(list);
    }

    /**
     * 导出系统临时附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:temp:export')")
    @Log(title = "系统临时附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFileTemp sysFileTemp) {
        List<SysFileTemp> list = sysFileTempService.selectSysFileTempList(sysFileTemp);
        ExcelUtil<SysFileTemp> util = new ExcelUtil<SysFileTemp>(SysFileTemp.class);
        util.exportExcel(response, list, "系统临时附件数据");
    }

    /**
     * 获取系统临时附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:temp:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(sysFileTempService.selectSysFileTempById(id));
    }

    /**
     * 获取系统临时附件详细信息 byIds
     */
    @PreAuthorize("@ss.hasPermi('system:temp:query')")
    @GetMapping(value = "/getFileTempByIds")
    public AjaxResult getFileTempByIds(SysFileTemp sysFileTemp) {
        String[] ids = ArrayUtil.toArray(sysFileTemp.getIds(), String.class);
        return AjaxResult.success(sysFileTempService.selectSysFileTempByIds(ids));
    }

    /**
     * 新增系统临时附件
     */
    @PreAuthorize("@ss.hasPermi('system:temp:add')")
    @Log(title = "系统临时附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFileTemp sysFileTemp) {
        return toAjax(sysFileTempService.insertSysFileTemp(sysFileTemp));
    }

    /**
     * 修改系统临时附件
     */
    @PreAuthorize("@ss.hasPermi('system:temp:edit')")
    @Log(title = "系统临时附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFileTemp sysFileTemp) {
        return toAjax(sysFileTempService.updateSysFileTemp(sysFileTemp));
    }

    /**
     * 删除系统临时附件
     */
    @PreAuthorize("@ss.hasPermi('system:temp:remove')")
    @Log(title = "系统临时附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(sysFileTempService.deleteSysFileTempByIds(ids));
    }

}
