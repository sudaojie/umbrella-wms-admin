package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 库区基本信息Controller
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@RestController
@RequestMapping("/basics/area")
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    /**
     * 查询库区基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:area:list')")
    @PostMapping("/selectAreas")
    public TableDataInfo selectAreas(@RequestBody Area area) {
        logger.info("/basics/area/selectAreas");
        startPage();
        List<Area> list = areaService.selectAreas(area);
        return getDataTable(list);
    }

    /**
     * 查询库区基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:area:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Area area) {
        logger.info("/basics/area/list");
        startPage();
        List<Area> list = areaService.selectAreaList(area);
        return getDataTable(list);
    }

    /**
     * 导出库区基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:area:export')")
    @Log(title = "库区基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Area area) {
        logger.info("/basics/area/export");
        List<Area> list = areaService.selectAreaList(area);
        ExcelUtil<Area> util = new ExcelUtil<Area>(Area.class);
        util.exportExcel(response, list, "库区基本信息数据");
    }

    /**
     * 获取库区基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/area/getInfo/id");
        return success(areaService.selectAreaById(id));
    }

    /**
     * 新增库区基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:add')")
    @Log(title = "库区基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Area area) {
        logger.info("/basics/area/add");
        return AjaxResult.success(areaService.insertArea(area));
    }

    /**
     * 修改库区基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:edit')")
    @Log(title = "库区基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Area area) {
        if(StringUtils.isEmpty(area.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/basics/area/edit");
        return AjaxResult.success(areaService.updateArea(area));
    }

    /**
     * 删除库区基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:remove')")
    @Log(title = "库区基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/area/remove/id");
        return areaService.deleteAreaByIds(ids);
    }

    /**
     * 检查库区信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:add')")
    @Log(title = "库区基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Area area) {
        logger.info("/basics/area/checkData");
        return areaService.checkData(area);
    }

    /**
     * 获取库区信息（有仓库获取该仓库下的库区，否则获取全部）
     */
    @PreAuthorize("@ss.hasPermi('basics:area:list')")
    @Log(title = "获取库区信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getAreaData")
    public AjaxResult getAreaData(@RequestBody Area area) {
        logger.info("/basics/area/getAreaData");
        return AjaxResult.success(areaService.getAreaData(area));
    }

    @Log(title = "仓库信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, String warehouseId) throws Exception {
        ExcelUtil<Area> util = new ExcelUtil<Area>(Area.class);
        List<Area> areaList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = areaService.importData(areaList, updateSupport, operName, warehouseId);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Area> util = new ExcelUtil<Area>(Area.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * wms参数配置 初始化获取库区信息
     */
    @Log(title = "wms参数配置 初始化获取库区信息", businessType = BusinessType.OTHER)
    @PostMapping("/findAreaData")
    public AjaxResult findAreaData() {
        logger.info("/basics/area/findAreaData");
        return AjaxResult.success(areaService.findAreaData());
    }
}
