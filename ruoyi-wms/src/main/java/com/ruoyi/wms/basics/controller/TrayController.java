package com.ruoyi.wms.basics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.dto.PrintDataDto;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.basics.dto.TrayDto;
import com.ruoyi.wms.exception.LocationNotException;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import com.ruoyi.wms.wcstask.vo.WaitTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 托盘基本信息Controller
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@RestController
@RequestMapping("/basics/tray")
public class TrayController extends BaseController {

    @Autowired
    private TrayService trayService;
    @Autowired
    private WaittaskService waittaskService;

    @Value("${zebra.print.remote}")
    private String zebraPrintWebUrl;

    //容量数据转换
    DecimalFormat df = new DecimalFormat("0.000000");

    /**
     * 查询托盘基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Tray tray) {
        logger.info("/basics/tray/list");
        startPage();
        List<Tray> list = trayService.selectTrayList(tray);
        return getDataTable(list);
    }

    /**
     * 导出托盘基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:export')")
    @Log(title = "托盘基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Tray tray) {
        logger.info("/basics/tray/export");
        List<Tray> list = trayService.selectTrayList(tray);
        ExcelUtil<Tray> util = new ExcelUtil<Tray>(Tray.class);
        util.exportExcel(response, list, "托盘基本信息数据");
    }

    /**
     * 导入托盘基本信息列表
     */
    @PreAuthorize(value = "@ss.hasPermi('basics:tray:import')")
    @Log(title = "托盘基本信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, String modelCode,String warehouse, String area, String location) throws Exception {
        logger.info("/basics/tray/import");
        ExcelUtil<Tray> util = new ExcelUtil<Tray>(Tray.class);
        List<Tray> trayList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = trayService.importData(trayList, updateSupport, operName,modelCode, warehouse, area, location);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Tray> util = new ExcelUtil<Tray>(Tray.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取托盘基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/tray/getInfo/id");
        return success(trayService.selectTrayById(id));
    }

    /**
     * 新增托盘基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:add')")
    @Log(title = "托盘基本信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Tray tray) {
        logger.info("/basics/tray/add");
        return AjaxResult.success(trayService.insertTray(tray));
    }

    /**
     * 修改托盘基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:edit')")
    @Log(title = "托盘基本信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Tray tray) {
        logger.info("/basics/tray/edit");
        return AjaxResult.success(trayService.updateTray(tray));
    }

    /**
     * 删除托盘基本信息
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:remove')")
    @Log(title = "托盘基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/tray/remove/id");
        return AjaxResult.success(trayService.deleteTrayByIds(ids));
    }

    /**
     * 检查托盘编码是否存在
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:query')")
    @Log(title = "托盘基本信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody Tray tray) {
        logger.info("/basics/tray/checkData");
        return trayService.checkData(tray);
    }

    /**
     * 生成二维码打印数据
     */
    @PreAuthorize("@ss.hasPermi('basics:tray:printer')")
    @Log(title = "生成二维码数据", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getPrintData")
    public AjaxResult getPrintData(@RequestBody Tray tray) throws IOException, WriterException {
        logger.info("/basics/tray/getPrintData");
        return AjaxResult.success(trayService.getPrintData(tray));
    }


    /**
     * 人工取盘
     */
    @Log(title = "人工取盘", businessType = BusinessType.OTHER)
    @PostMapping("/manMadeTakeTray")
    public AjaxResult manMadeTakeTray(@RequestBody TrayDto map) {
        logger.info("/basics/tray/manMadeTakeTray");
        return trayService.manMadeTakeTray(map);
    }

    /**
     * 智能取盘
     */
    @Log(title = "智能取盘", businessType = BusinessType.OTHER)
    @PostMapping("/aiTakeTray")
    public AjaxResult aiTakeTray(@RequestBody TrayDto map) {
        logger.info("/basics/tray/aiTakeTray");
        return trayService.aiTakeTray(map);
    }

    /**
     * 回盘
     */
    @Log(title = "回盘", businessType = BusinessType.OTHER)
    @PostMapping("/putTray")
    public AjaxResult putTray(@RequestBody TrayDto map) {
        logger.info("/basics/tray/putTray");
        AjaxResult ajaxResult = new AjaxResult();
        try {
            ajaxResult = trayService.putTray(map);
        } catch (LocationNotException e) {
            throw new ServiceException("存储区库位不足");
        }
        return ajaxResult;
    }

    /**
     * 查询agv启用状态
     */
    @PostMapping("/selectAgv")
    public AjaxResult selectAgv(@RequestBody WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        logger.info("/basics/tray/selectAgv");
        return trayService.selectAgv(wcsDeviceBaseInfo);
    }

    /**
     * 查看等待任务列表
     * @return
     */
    @GetMapping("/selectWaitTask")
    public TableDataInfo list() {
        logger.info("/basics/tray/list");
        startPage();
        List<WaitTaskVo> waitTaskVos = waittaskService.selectWaitTask();
        return getDataTable(waitTaskVos);
    }

    /**
     * 打印二维码
     */
    @Log(title = "打印二维码", businessType = BusinessType.OTHER)
    @PostMapping(value = "/printDataList")
    public AjaxResult printDataList(@RequestBody List<PrintDataDto> printDataDtos) throws Exception {
        logger.info("/basics/tray/printDataList");
        ObjectMapper objectMapper = new ObjectMapper();
        HttpUtils.sendPost(zebraPrintWebUrl+"/printTray",objectMapper.writeValueAsString(printDataDtos));
        return AjaxResult.success("success");
    }



}
