package com.ruoyi.wms.basics.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import com.ruoyi.wms.basics.service.GoodsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 货物信息Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/basics/goodsinfo")
public class GoodsInfoController extends BaseController {

    @Autowired
    private GoodsInfoService goodsInfoService;

    /**
     * 查询货物信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody GoodsInfo goodsInfo) {
        logger.info("/basics/goodsinfo/list");
        startPage();
        List<GoodsInfo> list = goodsInfoService.selectGoodsInfoList(goodsInfo);
        return getDataTable(list);
    }

    /**
     * 导出货物信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:export')")
    @Log(title = "货物信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GoodsInfo goodsInfo) {
        logger.info("/basics/goodsinfo/export");
        List<GoodsInfo> list = goodsInfoService.selectGoodsInfoList(goodsInfo);
        ExcelUtil<GoodsInfo> util = new ExcelUtil<GoodsInfo>(GoodsInfo.class);
        util.exportExcel(response, list, "货物信息数据");
    }

    /**
     * 导入货物信息列表
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:import')")
    @Log(title = "货物信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport, String goodsCategoryId, String supplierId) throws Exception {
        logger.info("/basics/goodsinfo/import");
        ExcelUtil<GoodsInfo> util = new ExcelUtil<GoodsInfo>(GoodsInfo.class);
        List<GoodsInfo> goodsInfoList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = goodsInfoService.importData(goodsInfoList, updateSupport, operName, goodsCategoryId, supplierId);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<GoodsInfo> util = new ExcelUtil<GoodsInfo>(GoodsInfo.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取货物信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/basics/goodsinfo/getInfo/id");
        return success(goodsInfoService.selectGoodsInfoById(id));
    }

    /**
     * 新增货物信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:add')")
    @Log(title = "货物信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody GoodsInfo goodsInfo) {
        logger.info("/basics/goodsinfo/add");
        return AjaxResult.success(goodsInfoService.insertGoodsInfo(goodsInfo));
    }

    /**
     * 修改货物信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:edit')")
    @Log(title = "货物信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody GoodsInfo goodsInfo) {

        logger.info("/basics/goodsinfo/edit");
        return goodsInfoService.updateGoodsInfo(goodsInfo);
    }

    /**
     * 删除货物信息
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:remove')")
    @Log(title = "货物信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/basics/goodsinfo/remove/id");
        return AjaxResult.success(goodsInfoService.deleteGoodsInfoByIds(ids));
    }

    /**
     * 检查货物信息
     */
    @PreAuthorize("@ss.hasPermi('basics:area:add')")
    @Log(title = "货物信息", businessType = BusinessType.OTHER)
    @PostMapping(value = "/checkData")
    public AjaxResult checkData(@RequestBody GoodsInfo goodsInfo) {
        logger.info("/basics/goodsinfo/checkData");
        return goodsInfoService.checkData(goodsInfo);
    }

    /**
     * 根据货物编码查询货物信息
     * @param goodsCode 货物类型编码
     * @return GoodsInfo
     */
    @PreAuthorize("@ss.hasPermi('basics:goodsinfo:query')")
    @GetMapping(value = "/getGoodsNum/{goodsCode}")
    public AjaxResult getGoodsNum(@PathVariable("goodsCode") String goodsCode) {
        logger.info("/basics/goodsinfo/getGoodsNum/goodsCode,货物类型编码:{}",goodsCode);
        return success(goodsInfoService.getGoodsNum(goodsCode));
    }
}
