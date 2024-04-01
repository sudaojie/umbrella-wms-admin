package com.ruoyi.wms.warehousing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import com.ruoyi.wms.warehousing.dto.PartsPrintDto;
import com.ruoyi.wms.warehousing.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;


/**
 * 机件号记录Controller
 *
 * @author nf
 * @date 2023-02-14
 */
@RestController
@RequestMapping("/warehousing/parts")
public class PartsController extends BaseController {

    @Autowired
    private PartsService partsService;

    @Value("${zebra.print.remote}")
    private String zebraPrintWebUrl;

    /**
     * 查询机件号记录列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Parts parts) {
        logger.info("/warehousing/parts/list");
        startPage();
        List<Parts> list = partsService.selectPartsList(parts);
        return getDataTable(list);
    }

    /**
     * 级联获取物品类型
     * @param parts
     * @return
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:list')")
    @PostMapping("/getCategoryCode")
    public AjaxResult getCategoryCode(@RequestBody Parts parts) {
        logger.info("/warehousing/parts/getCategoryCode");
        List<Parts> list = partsService.getCategoryCode(parts);
        return AjaxResult.success(list);
    }

    /**
     * 导出机件号记录列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:export')")
    @Log(title = "机件号记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Parts parts){
        logger.info("/warehousing/parts/export");
        List<Parts> list = partsService.findPartsList(parts);
        ExcelUtil<Parts> util = new ExcelUtil<Parts>(Parts.class);
        util.exportExcel(response, list, "机件号记录数据");
    }
    /**
     * 导入机件号记录列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:import')")
    @Log(title = "机件号记录", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/warehousing/parts/import");
        ExcelUtil<Parts> util = new ExcelUtil<Parts>(Parts.class);
        List<Parts> partsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = partsService.importData(partsList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 下载模板
     * @param parts
     * @param response
     */
    @PostMapping("/importTemplate")
    public void importTemplate(Parts parts, HttpServletResponse response){
        ExcelUtil<Parts> util = new ExcelUtil<Parts>(Parts.class);
        List<Parts> list = partsService.importPartsList(parts);
        util.exportExcel(response, list, "模板数据");
    }

    /**
     * 获取机件号记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/warehousing/parts/getInfo/id");
        return success(partsService.selectPartsById(id));
    }

    /**
     * 新增机件号记录
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:add')")
    @Log(title = "机件号记录", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody Parts parts){
        logger.info("/warehousing/parts/add");
        return AjaxResult.success(partsService.insertParts(parts));
    }

    /**
     * 修改机件号记录
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:edit')")
    @Log(title = "机件号记录", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody List<InbillGoods> parts) throws ParseException {
        logger.info("/warehousing/parts/edit");
        return AjaxResult.success(partsService.updateParts(parts));
    }

    /**
     * 修改打印状态
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:edit')")
    @Log(title = "机件号记录", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/editPrint")
    public AjaxResult editPrint(@RequestBody Parts parts){
        logger.info("/warehousing/parts/editPrint");
        return AjaxResult.success(partsService.updatePartsPrint(parts));
    }

    /**
     * 删除机件号记录
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:remove')")
    @Log(title = "机件号记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/warehousing/parts/remove/id");
        return AjaxResult.success(partsService.deletePartsByIds(ids));
    }
    /**
     * 生成二维码打印数据
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:printer')")
    @Log(title = "生成二维码数据", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getPrintData")
    public AjaxResult getPrintData(@RequestBody Parts parts) throws IOException, WriterException, ParseException  {
        logger.info("/warehousing/parts/getPrintData");
        return partsService.getPrintData(parts);
    }

    /**
     * IO控制查询机件号
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:printer')")
    @Log(title = "IO控制查询机件号", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getIoPrintData")
    public AjaxResult getIoPrintData(@RequestBody Parts parts) throws IOException, WriterException {
        logger.info("/warehousing/parts/getIoPrintData");
        return partsService.getIoPrintData(parts);
    }

    /**
     * 查询机件号记录展示列表
     * @param parts
     * @return
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:query')")
    @PostMapping("/findPartsList")
    public TableDataInfo findPartsList(@RequestBody Parts parts) {
        logger.info("/warehousing/parts/findPartsList");
        startPage();
        return getDataTable(partsService.findPartsList(parts));
    }

    /**
     * 新增查询机件号记录展示列表
     * @param parts
     * @return
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:query')")
    @PostMapping("/findAddPartsList")
    public TableDataInfo findAddPartsList(@RequestBody Parts parts) {
        logger.info("/warehousing/parts/findAddPartsList");
        startPage();
        List<Parts> list = partsService.findAddPartsList(parts);
        return getDataTable(list);
    }

    /**
     * 初始化获取入库单号
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:query')")
    @Log(title = "初始化获取入库单号", businessType = BusinessType.OTHER)
    @PostMapping(value = "/findInbillCode")
    public AjaxResult findInbillCode() {
        logger.info("/warehousing/parts/findInbillCode");
        return AjaxResult.success(partsService.findInbillCode());
    }

    /**
     * 新增初始化获取入库单号
     */
    @PreAuthorize("@ss.hasPermi('warehousing:parts:query')")
    @Log(title = "新增初始化获取入库单号", businessType = BusinessType.OTHER)
    @PostMapping(value = "/findAddInbillCode")
    public AjaxResult findAddInbillCode() {
        logger.info("/warehousing/parts/findAddInbillCode");
        return AjaxResult.success(partsService.findAddInbillCode());
    }

    /**
     * 打印二维码
     */
    @Log(title = "打印二维码", businessType = BusinessType.OTHER)
    @PostMapping(value = "/printDataList")
    public AjaxResult printDataList(@RequestBody List<PartsPrintDto> partsPrintDtos) throws Exception {
        logger.info("/warehousing/parts/printDataList");
        ObjectMapper objectMapper = new ObjectMapper();
        HttpUtils.sendPost(zebraPrintWebUrl+"/printParts",objectMapper.writeValueAsString(partsPrintDtos));
        return AjaxResult.success("success");
    }

}
