package com.ruoyi.wms.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.enums.WarningConfigEnum;
import com.ruoyi.wms.stock.domain.WarehouseWarning;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import com.ruoyi.wms.stock.mapper.WarehouseWarningMapper;
import com.ruoyi.wms.stock.service.WarehouseWarningService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import java.util.List;

/**
 * 库存预警Controller
 *
 * @author ruoyi
 * @date 2023-03-10
 */
@RestController
@RequestMapping("/stock/warning")
public class WarningController extends BaseController {

    @Autowired
    private WarehouseWarningService warehouseWarningService;
    @Autowired
    private WarehouseWarningMapper warehouseWarningMapper;

    /**
     * 查询库存总览列表
     */
    @PreAuthorize("@ss.hasPermi('stock:warning:validityList')")
    @PostMapping("/validityList")
    public TableDataInfo validityList(@RequestBody WarehouseWarning warehouseWarning) {
        logger.info("/stock/warning/validityList");
        WmsWarningConfig wmsWarningConfig = warehouseWarningService.getConfig();
        warehouseWarning.setWarningProxy(wmsWarningConfig.getConfigValue());
        startPage();
        List<WarehouseWarning> list = warehouseWarningService.selectWmsValidityList(warehouseWarning);
        return getDataTable(list);
    }

    /**
     * 查询库存有效期列表
     */
    @PreAuthorize("@ss.hasPermi('stock:warning:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WarehouseWarning warehouseWarning) {
        logger.info("/stock/warning/list");
        startPage();
        List<WarehouseWarning> list = warehouseWarningService.selectWmsWarehouseTblstockList(warehouseWarning);
        return getDataTable(list);
    }

    /**
     * 查询库存滞压预警列表
     */
    @PreAuthorize("@ss.hasPermi('stock:warning:list')")
    @PostMapping("/detainedList")
    public TableDataInfo detainedList(@RequestBody WarehouseWarning warehouseWarning) {
        logger.info("/stock/warning/detainedList");
        startPage();
        List<WarehouseWarning> list = warehouseWarningService.selectDetainedList(warehouseWarning);
        return getDataTable(list);
    }

    /**
     * 获取库存总览详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:warning:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/stock/warning/getInfo/id");
        return success(warehouseWarningService.selectWmsWarehouseTblstockById(id));
    }

}
