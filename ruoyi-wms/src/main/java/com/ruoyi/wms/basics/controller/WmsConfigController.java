package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.basics.dto.WmsTacticsConfigEditDto;
import com.ruoyi.wms.basics.service.WmsTacticsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * wms参数配置Controller
 *
 * @author ruoyi
 * @date 2023-02-23
 */
@RestController
@RequestMapping("/wms/config")
public class WmsConfigController extends BaseController {

    @Autowired
    private WmsTacticsConfigService wmsTacticsConfigService;

    @Autowired
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    /**
     * 查询WCS设备基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:config:list')")
    @PostMapping("/listAGV")
    public TableDataInfo listAGV() {
        WcsDeviceBaseInfo wcsDeviceBaseInfo = new WcsDeviceBaseInfo();
        wcsDeviceBaseInfo.setDeviceType(WcsTaskDeviceTypeEnum.AVG.getCode());
        List<WcsDeviceBaseInfo> list = wcsDeviceBaseInfoService.selectWcsDeviceBaseInfoList(wcsDeviceBaseInfo);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(0);
        return rspData;
    }

    /**
     * 查询货物类型托盘取盘回盘策略配置列表
     */
    @PreAuthorize("@ss.hasPermi('basics:config:list')")
    @PostMapping("/listConfig")
    public TableDataInfo list(@RequestBody WmsTacticsConfig wmsTacticsConfig) {
        logger.info("/wms/config/list");
        startPage();
        List<WmsTacticsConfig> list = wmsTacticsConfigService.selectWmsTacticsConfigList(wmsTacticsConfig);
        return getDataTable(list);
    }


    /**
     * 更新策略
     */
    @PreAuthorize("@ss.hasPermi('basics:config:edit')")
    @PutMapping("/editConfig")
    public AjaxResult editConfig(@RequestBody WmsTacticsConfigEditDto wmsTacticsConfigEditDto) {
        logger.info("/wms/config/editConfig");
        return AjaxResult.success(wmsTacticsConfigService.updateWmsTacticsConfig(wmsTacticsConfigEditDto));
    }


}
