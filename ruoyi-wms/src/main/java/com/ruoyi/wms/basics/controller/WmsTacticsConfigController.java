package com.ruoyi.wms.basics.controller;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.basics.service.WmsTacticsConfigService;
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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.List;


/**
 * 货物类型托盘取盘回盘策略配置Controller
 *
 * @author ruoyi
 * @date 2023-02-24
 */
@RestController
@RequestMapping("/basics/config")
public class WmsTacticsConfigController extends BaseController {

    @Autowired
    private WmsTacticsConfigService wmsTacticsConfigService;

    /**
     * 查询货物类型托盘取盘回盘策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('basics:config:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsTacticsConfig wmsTacticsConfig) {
        logger.info("/basics/config/list");
        startPage();
        List<WmsTacticsConfig> list = wmsTacticsConfigService.selectWmsTacticsConfigList(wmsTacticsConfig);
        return getDataTable(list);
    }

    /**
     * 导出货物类型托盘取盘回盘策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('basics:config:export')")
    @Log(title = "货物类型托盘取盘回盘策略配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsTacticsConfig wmsTacticsConfig) {
        logger.info("/basics/config/export");
        List<WmsTacticsConfig> list = wmsTacticsConfigService.selectWmsTacticsConfigList(wmsTacticsConfig);
        ExcelUtil<WmsTacticsConfig> util = new ExcelUtil<WmsTacticsConfig>(WmsTacticsConfig.class);
        util.exportExcel(response, list, "货物类型托盘取盘回盘策略配置数据");
    }

    /**
     * 导入货物类型托盘取盘回盘策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('basics:config:import')")
    @Log(title = "货物类型托盘取盘回盘策略配置", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/basics/config/import");
        ExcelUtil<WmsTacticsConfig> util = new ExcelUtil<WmsTacticsConfig>(WmsTacticsConfig.class);
        List<WmsTacticsConfig> wmsTacticsConfigList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsTacticsConfigService.importData(wmsTacticsConfigList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<WmsTacticsConfig> util = new ExcelUtil<WmsTacticsConfig>(WmsTacticsConfig.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取货物类型托盘取盘回盘策略配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:config:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/config/getInfo/id");
        return success(wmsTacticsConfigService.selectWmsTacticsConfigById(id));
    }

    /**
     * 新增货物类型托盘取盘回盘策略配置
     */
    @PreAuthorize("@ss.hasPermi('basics:config:configSave')")
    @Log(title = "货物类型托盘取盘回盘策略配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/configSave")
    public AjaxResult configSave(@RequestBody WmsTacticsConfig wmsTacticsConfig) {
        logger.info("/basics/config/configSave");
        return AjaxResult.success(wmsTacticsConfigService.insertWmsTacticsConfig(wmsTacticsConfig));
    }


    /**
     * 删除货物类型托盘取盘回盘策略配置
     */
    @PreAuthorize("@ss.hasPermi('basics:config:remove')")
    @Log(title = "货物类型托盘取盘回盘策略配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/config/remove/id");
        return AjaxResult.success(wmsTacticsConfigService.deleteWmsTacticsConfigByIds(ids));
    }

}
