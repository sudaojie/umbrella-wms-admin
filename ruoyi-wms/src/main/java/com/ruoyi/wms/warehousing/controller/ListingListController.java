package com.ruoyi.wms.warehousing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.warehousing.domain.ListingList;
import com.ruoyi.wms.warehousing.service.ListingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 上架单Controller
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@RestController
@RequestMapping("/warehousing/listinglist")
public class ListingListController extends BaseController {

    @Autowired
    private ListingListService listingListService;

    /**
     * 查询上架单列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ListingList listingList) {
        logger.info("/warehousing/listinglist/list");
        startPage();
        List<ListingList> list = listingListService.selectListingListList(listingList);
        return getDataTable(list);
    }

    /**
     * 导出上架单列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:export')")
    @Log(title = "上架单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ListingList listingList) {
        logger.info("/warehousing/listinglist/export");
        List<ListingList> list = listingListService.selectListingListList(listingList);
        ExcelUtil<ListingList> util = new ExcelUtil<ListingList>(ListingList.class);
        util.exportExcel(response, list, "上架单数据");
    }

    /**
     * 获取上架单详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/warehousing/listinglist/getInfo/id");
        return success(listingListService.selectListingListById(id));
    }

    /**
     * 新增上架单
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:add')")
    @Log(title = "上架单", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody ListingList listingList) {
        logger.info("/warehousing/listinglist/add");
        return AjaxResult.success(listingListService.insertListingList(listingList));
    }

    /**
     * 修改上架单
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:edit')")
    @Log(title = "上架单", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody ListingList listingList) {
        logger.info("/warehousing/listinglist/edit");
        return AjaxResult.success(listingListService.updateListingList(listingList));
    }

    /**
     * 删除上架单
     */
    @PreAuthorize("@ss.hasPermi('warehousing:listinglist:remove')")
    @Log(title = "上架单", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/warehousing/listinglist/remove/id");
        return AjaxResult.success(listingListService.deleteListingListByIds(ids));
    }

}
