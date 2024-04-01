package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Vehicle;
import com.ruoyi.wms.basics.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 车辆基本信息Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/basics/vehicle")
public class VehicleController extends BaseController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 查询车辆基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Vehicle vehicle) {
        logger.info("/basics/vehicle/list");
        startPage();
        List<Vehicle> list = vehicleService.selectVehicleList(vehicle);
        return getDataTable(list);
    }

    /**
     * 导出车辆基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:export')")
    @Log(title = "车辆基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Vehicle vehicle) {
        logger.info("/basics/vehicle/export");
        List<Vehicle> list = vehicleService.selectVehicleList(vehicle);
        ExcelUtil<Vehicle> util = new ExcelUtil<Vehicle>(Vehicle.class);
        util.exportExcel(response, list, "车辆基本信息数据");
    }

    /**
     * 导入车辆基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:import')")
    @Log(title = "车辆基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/basics/vehicle/import");
        ExcelUtil<Vehicle> util = new ExcelUtil<Vehicle>(Vehicle.class);
        List<Vehicle> vehicleList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = vehicleService.importData(vehicleList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Vehicle> util = new ExcelUtil<Vehicle>(Vehicle.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取车辆基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/vehicle/getInfo/id");
        return success(vehicleService.selectVehicleById(id));
    }

    /**
     * 新增车辆基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:add')")
    @Log(title = "车辆基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Vehicle vehicle) {
        logger.info("/basics/vehicle/add");
        return AjaxResult.success(vehicleService.insertVehicle(vehicle));
    }

    /**
     * 修改车辆基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:edit')")
    @Log(title = "车辆基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Vehicle vehicle) {
        logger.info("/basics/vehicle/edit");
        return AjaxResult.success(vehicleService.updateVehicle(vehicle));
    }

    /**
     * 删除车辆基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:remove')")
    @Log(title = "车辆基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/vehicle/remove/id");
        return AjaxResult.success(vehicleService.deleteVehicleByIds(ids));
    }

    /**
     * 检查车辆基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:vehicle:query')")
    @Log(title = "车辆基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Vehicle vehicle) {
        logger.info("/basics/vehicle/checkData");
        return vehicleService.checkData(vehicle);
    }

    /**
     * 获取全部车辆
     */
    @Log(title = "车辆基本信息", businessType = BusinessType.DELETE)
    @PostMapping("/getVehicleList")
    public AjaxResult getVehicleList() {
        logger.info("/basics/vehicle/getVehicleList");
        return AjaxResult.success(vehicleService.getVehicleList());
    }
}
