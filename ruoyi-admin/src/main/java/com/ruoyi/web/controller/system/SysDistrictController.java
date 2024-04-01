package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysDistrict;
import com.ruoyi.system.service.impl.SysDistrictServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 行政区划Controller
 *
 * @author hewei
 * @date 2023-01-04
 */
@RestController
@RequestMapping("/system/district")
public class SysDistrictController extends BaseController {

    @Autowired
    private SysDistrictServiceImpl sysDistrictService;

    /**
     * 查询行政区划列表
     */
    @PreAuthorize("@ss.hasPermi('system:district:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SysDistrict sysDistrict) {
        startPage();
        List<SysDistrict> list = sysDistrictService.selectSysDistrictList(sysDistrict);
        return getDataTable(list);
    }

    /**
     * 导出行政区划列表
     */
    @PreAuthorize("@ss.hasPermi('system:district:export')")
    @Log(title = "行政区划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDistrict sysDistrict){
        List<SysDistrict> list = sysDistrictService.selectSysDistrictList(sysDistrict);
        ExcelUtil<SysDistrict> util = new ExcelUtil<SysDistrict>(SysDistrict.class);
        util.exportExcel(response, list, "行政区划数据");
    }

    /**
     * 获取行政区划详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:district:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        return success(sysDistrictService.selectSysDistrictById(id));
    }

    /**
     * 新增行政区划
     */
    @PreAuthorize("@ss.hasPermi('system:district:add')")
    @Log(title = "行政区划", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody SysDistrict sysDistrict){
        return AjaxResult.success(sysDistrictService.insertSysDistrict(sysDistrict));
    }

    /**
     * 修改行政区划
     */
    @PreAuthorize("@ss.hasPermi('system:district:edit')")
    @Log(title = "行政区划", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody SysDistrict sysDistrict){
        return AjaxResult.success(sysDistrictService.updateSysDistrict(sysDistrict));
    }

    /**
     * 删除行政区划
     */
    @PreAuthorize("@ss.hasPermi('system:district:remove')")
    @Log(title = "行政区划", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        return AjaxResult.success(sysDistrictService.deleteSysDistrictByIds(ids));
    }

    /**
     * 查询行政区划子级列表
     */
    @PreAuthorize("@ss.hasPermi('system:district:list')")
    @GetMapping("/listChildren")
    public AjaxResult listChildren(@RequestParam("pId") String pId) {
        return AjaxResult.success(sysDistrictService.listChildren(pId));
    }

    /**
     * 查询行政区划子级列表
     */
    @PreAuthorize("@ss.hasPermi('system:district:list')")
    @GetMapping("/listChildrenIds")
    public AjaxResult listChildrenIds(@RequestParam("pIds") String pIds) {
        return AjaxResult.success(sysDistrictService.listChildrenIds(pIds));
    }

}
