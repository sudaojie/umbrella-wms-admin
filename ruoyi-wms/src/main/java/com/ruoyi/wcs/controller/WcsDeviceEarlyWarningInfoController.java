package com.ruoyi.wcs.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.wcs.domain.dto.WcsDeviceEarlyWarningFormDto;
import com.ruoyi.wcs.service.WcsDeviceEarlyWarningInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;

import java.util.List;


/**
 * 设备预警信息Controller
 *
 * @author hewei
 * @date 2023-04-17
 */
@RestController
@RequestMapping("/wcs/deviceEarlyWarningInfo")
public class WcsDeviceEarlyWarningInfoController extends BaseController {

    @Autowired
    private WcsDeviceEarlyWarningInfoService wcsDeviceEarlyWarningInfoService;

    /**
     * 查询WCS设备预警列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WcsDeviceEarlyWarningFormDto wcsDeviceEarlyWarningFormDto) {
        logger.info("/wcs/deviceEarlyWarningInfo/list");
        startPage();
        List<WcsDeviceEarlyWarningFormDto> list = wcsDeviceEarlyWarningInfoService.selectWcsDeviceBaseInfoList(wcsDeviceEarlyWarningFormDto);
        return getDataTable(list);
    }

    /**
     * 详情
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo(@RequestParam String id) {
        logger.info("/wcs/deviceEarlyWarningInfo/getInfo");
        return AjaxResult.success(wcsDeviceEarlyWarningInfoService.getInfo(id));
    }

}
