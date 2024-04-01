package com.ruoyi.wms.stock.controller;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.stock.domain.WmsWarningConfig;
import com.ruoyi.wms.stock.service.WmsWarningConfigService;
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
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.List;


/**
 * 库存预警策略Controller
 *
 * @author ruoyi
 * @date 2023-03-13
 */
@RestController
@RequestMapping("/warning/config")
public class WmsWarningConfigController extends BaseController {

    @Autowired
    private WmsWarningConfigService wmsWarningConfigService;

    /**
     * 查询库存预警策略列表
     */
    @PreAuthorize("@ss.hasPermi('warning:config:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsWarningConfig wmsWarningConfig) {
        logger.info("/warning/config/list");
        startPage();
        List<WmsWarningConfig> list = wmsWarningConfigService.selectWmsWarningConfigList(wmsWarningConfig);
        return getDataTable(list);
    }

    /**
     * 获取库存预警策略详细信息
     */
    @PreAuthorize("@ss.hasPermi('warning:config:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String ID){
        logger.info("/warning/config/getInfo/ID");
        return success(wmsWarningConfigService.selectWmsWarningConfigByID(ID));
    }

    /**
     * 新增库存预警策略
     */
    @PreAuthorize("@ss.hasPermi('warning:config:add')")
    @Log(title = "库存预警策略", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsWarningConfig wmsWarningConfig){
        logger.info("/warning/config/add");
        return AjaxResult.success(wmsWarningConfigService.insertWmsWarningConfig(wmsWarningConfig));
    }

    /**
     * 修改库存预警策略
     */
    @PreAuthorize("@ss.hasPermi('warning:config:edit')")
    @Log(title = "库存预警策略", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsWarningConfig wmsWarningConfig){
        if(StringUtils.isEmpty(wmsWarningConfig.getId())){
            throw new RuntimeException("修改失败，数据主键id缺失");
        }
        logger.info("/warning/config/edit");
        return AjaxResult.success(wmsWarningConfigService.updateWmsWarningConfig(wmsWarningConfig));
    }

    /**
     * 删除库存预警策略
     */
    @PreAuthorize("@ss.hasPermi('warning:config:remove')")
    @Log(title = "库存预警策略", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{IDs}")
    public AjaxResult remove(@PathVariable String[] IDs){
        logger.info("/warning/config/remove/ID");
        return AjaxResult.success(wmsWarningConfigService.deleteWmsWarningConfigByIDs(IDs));
    }

}
