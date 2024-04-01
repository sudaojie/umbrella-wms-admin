package com.ruoyi.wms.warehousing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.dto.InBillDto;
import com.ruoyi.wms.warehousing.service.ListingDetailService;
import com.ruoyi.wms.warehousing.vo.ListingDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 上架单详情Controller
 *
 * @author ruoyi
 * @date 2023-02-03
 */
@RestController
@RequestMapping("/warehousing/listingdetail")
public class ListingDetailController extends BaseController {

    @Autowired
    private ListingDetailService listingDetailService;

    /**
     * 查询上架单详情列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ListingDetail listingDetail) {
        logger.info("/warehousing/listingdetail/list");
        startPage();
        List<ListingDetail> list = listingDetailService.selectListingDetailList(listingDetail);
        return getDataTable(list);
    }

    /**
     * 导出上架单详情列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:export')")
    @Log(title = "上架单详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListingDetail listingDetail) {
        logger.info("/warehousing/listingdetail/export");
        List<ListingDetail> list = listingDetailService.selectListingDetailList(listingDetail);
        ExcelUtil<ListingDetail> util = new ExcelUtil<ListingDetail>(ListingDetail.class);
        util.exportExcel(response, list, "上架单详情数据");
    }

    /**
     * 导入上架单详情列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:import')")
    @Log(title = "上架单详情", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/warehousing/listingdetail/import");
        ExcelUtil<ListingDetail> util = new ExcelUtil<ListingDetail>(ListingDetail.class);
        List<ListingDetail> listingDetailList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = listingDetailService.importData(listingDetailList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<ListingDetail> util = new ExcelUtil<ListingDetail>(ListingDetail.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取上架单详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/warehousing/listingdetail/getInfo/id");
        return success(listingDetailService.selectListingDetailById(id));
    }

    /**
     * 新增上架单详情
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:add')")
    @Log(title = "上架单详情", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody ListingDetail listingDetail) {
        logger.info("/warehousing/listingdetail/add");
        return AjaxResult.success(listingDetailService.insertListingDetail(listingDetail));
    }

    /**
     * 修改上架单详情
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:edit')")
    @Log(title = "上架单详情", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody ListingDetail listingDetail) {
        logger.info("/warehousing/listingdetail/edit");
        return AjaxResult.success(listingDetailService.updateListingDetail(listingDetail));
    }

    /**
     * 删除上架单详情
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listingdetail:remove')")
    @Log(title = "上架单详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/warehousing/listingdetail/remove/id");
        return AjaxResult.success(listingDetailService.deleteListingDetailByIds(ids));
    }

    /**
     * 查询上架单详情列表
     */
    @PostMapping("/selectPutOnData")
    public TableDataInfo selectPutOnData(@RequestBody ListingDetail listingDetail) {
        logger.info("/warehousing/listingdetail/selectPutOnData");
        startPage();
        List<ListingDetailVo> list = listingDetailService.selectPutOnData(listingDetail);
        return getDataTable(list);
    }

    /**
     * 待上架列表
     */
    @PostMapping("/waitListingList")
    public TableDataInfo waitListingList(@RequestBody InBill inBill) {
        logger.info("/warehousing/listingdetail/waitListingList");
        startPage();
        List<InBillDto> mapList = listingDetailService.waitListingList(inBill);
        return getDataTable(mapList);
    }

}
