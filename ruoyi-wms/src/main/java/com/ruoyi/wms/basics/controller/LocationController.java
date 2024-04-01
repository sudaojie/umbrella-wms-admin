package com.ruoyi.wms.basics.controller;

import com.google.zxing.WriterException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.service.AreaService;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.enums.AreaTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 库位基本信息Controller
 *
 * @author ruoyi
 * @date 2023-01-31
 */
@RestController
@RequestMapping("/basics/location")
public class LocationController extends BaseController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private AreaService areaService;

    /**
     * 查询库位基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:location:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Location location) {
        logger.info("/basics/location/list");
        startPage();
        List<Location> list = locationService.selectLocationList(location);
        return getDataTable(list);
    }
    /**
     * 获取可选择的库位列表
     */
    @PostMapping("/listByParams")
    public TableDataInfo listByParams(@RequestBody Location location) {
        logger.info("/basics/location/listByParams");
        //获取存储区库位
        List<String> areaIds = areaService.selectAreaCodeByType(AreaTypeEnum.CCQ.getCode());
        startPage();
        List<Location> list = locationService.listByParams(location,areaIds);
        return getDataTable(list);
    }
    /**
     * 获取可选择的库位列表
     */
    @PostMapping("/listsByParams")
    public List<Location> listsByParams(@RequestBody Location location) {
        logger.info("/basics/location/listsByParams");
        //获取存储区库位
        List<String> areaIds = areaService.selectAreaCodeByType(AreaTypeEnum.CCQ.getCode());
        List<Location> list = locationService.listsByParams(location,areaIds);
        return list;
    }

    /**
     * 导出库位基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:location:export')")
    @Log(title = "库位基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Location location) {
        logger.info("/basics/location/export");
        List<Location> list = locationService.selectLocationList(location);
        ExcelUtil<Location> util = new ExcelUtil<Location>(Location.class);
        util.exportExcel(response, list, "库位基本信息数据");
    }

    /**
     * 获取库位基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:location:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/location/getInfo/id");
        return success(locationService.selectLocationById(id));
    }

    /**
     * 新增库位基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:location:add')")
    @Log(title = "库位基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Location location) {
        logger.info("/basics/location/add");
        return AjaxResult.success(locationService.insertLocation(location));
    }

    /**
     * 修改库位基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:location:edit')")
    @Log(title = "库位基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Location location) {
        logger.info("/basics/location/edit");
        return AjaxResult.success(locationService.updateLocation(location));
    }

    /**
     * 删除库位基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:location:remove')")
    @Log(title = "库位基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/location/remove/id");
        return locationService.deleteLocationByIds(ids);
    }

    /**
     * 检查库位信息
     */
    @PreAuthorize("@ss.hasPermi('basics:location:query')")
    @Log(title = "库位基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Location location) {
        logger.info("/basics/location/checkData");
        return locationService.checkData(location);
    }

    @Log(title = "库位基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, String warehouseId, String areaId) throws Exception {
        ExcelUtil<Location> util = new ExcelUtil<Location>(Location.class);
        List<Location> locationList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = locationService.importData(locationList, updateSupport, operName, warehouseId, areaId);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Location> util = new ExcelUtil<Location>(Location.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取库区信息（有仓库获取该仓库下的库区，否则获取全部）
     */
    @PreAuthorize("@ss.hasPermi('basics:location:list')")
    @Log(title = "获取库位基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getLocationData")
    public AjaxResult getLocationData(@RequestBody Location location) {
        logger.info("/basics/location/getAreaData");
        return AjaxResult.success(locationService.getLocationData(location));
    }

    /**
     * 堆垛机信息接口
     */
//    @PreAuthorize("@ss.hasPermi('basics:tray:stackerInfo')")
    @Log(title = "堆垛机信息接口", businessType = BusinessType.OTHER)
    @PostMapping("/stackerInfo")
    public WmsWcsInfo stackerInfo(@RequestBody WmsWcsInfo wmsWcsInfo) {
        logger.info("/basics/location/stackerInfo");
        try {
            return locationService.stackerInfo(wmsWcsInfo);
        }catch (Exception e){
            wmsWcsInfo.put(WmsWcsInfo.CODE, HttpStatus.ERROR);
            wmsWcsInfo.put(WmsWcsInfo.MSG, "执行失败");
            return wmsWcsInfo;
        }
    }

    /**
     * AGV信息接口
     */
//    @PreAuthorize("@ss.hasPermi('basics:tray:agvInfo')")
    @Log(title = "AGV信息接口", businessType = BusinessType.OTHER)
    @PostMapping("/agvInfo")
    public WmsWcsInfo agvInfo(@RequestBody WmsWcsInfo wmsWcsInfo) {
        logger.info("/basics/location/agvInfo");
        try {
            return locationService.agvInfo(wmsWcsInfo);
        }catch (Exception e){
            wmsWcsInfo.put(WmsWcsInfo.CODE, HttpStatus.ERROR);
            wmsWcsInfo.put(WmsWcsInfo.MSG, "执行失败");
            return wmsWcsInfo;
        }
    }
}
