package com.ruoyi.wms.check.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.wms.check.domain.CheckConfig;
import com.ruoyi.wms.check.service.CheckConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 盘点配置Controller
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@RestController
@RequestMapping("/check/checkConfig")
public class CheckConfigController extends BaseController {

    @Autowired
    private CheckConfigService checkConfigService;

    /**
     * 查询盘点配置列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CheckConfig checkConfig) {
        logger.info("/check/checkConfig/list");
        startPage();
        List<CheckConfig> list = checkConfigService.selectCheckConfigList(checkConfig);
        return getDataTable(list);
    }

    /**
     * 导出盘点配置列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:export')")
    @Log(title = "盘点配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CheckConfig checkConfig){
        logger.info("/check/checkConfig/export");
        List<CheckConfig> list = checkConfigService.selectCheckConfigList(checkConfig);
        ExcelUtil<CheckConfig> util = new ExcelUtil<CheckConfig>(CheckConfig.class);
        util.exportExcel(response, list, "盘点配置数据");
    }
    /**
     * 导入盘点配置列表
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:import')")
    @Log(title = "盘点配置", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/check/checkConfig/import");
        ExcelUtil<CheckConfig> util = new ExcelUtil<CheckConfig>(CheckConfig.class);
        List<CheckConfig> checkConfigList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = checkConfigService.importData(checkConfigList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CheckConfig> util = new ExcelUtil<CheckConfig>(CheckConfig.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取盘点配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/check/checkConfig/getInfo/id");
        return success(checkConfigService.selectCheckConfigById(id));
    }

    /**
     * 新增盘点配置
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:add')")
    @Log(title = "盘点配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody CheckConfig checkConfig){
        logger.info("/check/checkConfig/add");
        return AjaxResult.success(checkConfigService.insertCheckConfig(checkConfig));
    }

    /**
     * 修改盘点配置
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:edit')")
    @Log(title = "盘点配置", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody CheckConfig checkConfig){
        if(StringUtils.isEmpty(checkConfig.getId())){
            return AjaxResult.error("修改失败，数据主键id缺失");
        }
        logger.info("/check/checkConfig/edit");
        return AjaxResult.success(checkConfigService.updateCheckConfig(checkConfig));
    }

    /**
     * 删除盘点配置
     */
    @PreAuthorize("@ss.hasPermi('check:checkConfig:remove')")
    @Log(title = "盘点配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/check/checkConfig/remove/id");
        return AjaxResult.success(checkConfigService.deleteCheckConfigByIds(ids));
    }

}
