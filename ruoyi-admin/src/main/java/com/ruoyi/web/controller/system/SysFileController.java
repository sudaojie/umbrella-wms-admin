package com.ruoyi.web.controller.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 系统附件Controller
 *
 * @author yangjie
 * @date 2022-10-28
 */
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询系统附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFile sysFile) {
        startPage();
        List<SysFile> list = sysFileService.selectSysFileList(sysFile);
        return getDataTable(list);
    }

    /**
     * 导出系统附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:export')")
    @Log(title = "系统附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFile sysFile) {
        List<SysFile> list = sysFileService.selectSysFileList(sysFile);
        ExcelUtil<SysFile> util = new ExcelUtil<SysFile>(SysFile.class);
        util.exportExcel(response, list, "系统附件数据");
    }

    /**
     * 获取系统附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(sysFileService.selectSysFileById(id));
    }

    /**
     * 获取系统附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @PostMapping(value = "/getFileByIds")
    public AjaxResult getFileByIds(@RequestBody SysFile sysFile) {
        String[] ids = ArrayUtil.toArray(sysFile.getIds(), String.class);
        return AjaxResult.success(sysFileService.selectSysFileByIds(ids));
    }

    /**
     * 新增系统附件
     */
    @PreAuthorize("@ss.hasPermi('system:file:add')")
    @Log(title = "系统附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFile sysFile) {
        return toAjax(sysFileService.insertSysFile(sysFile));
    }

    /**
     * 修改系统附件
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "系统附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFile sysFile) {
        return toAjax(sysFileService.updateSysFile(sysFile));
    }

    /**
     * 删除系统附件
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "系统附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(sysFileService.deleteSysFileByIds(ids));
    }

}
