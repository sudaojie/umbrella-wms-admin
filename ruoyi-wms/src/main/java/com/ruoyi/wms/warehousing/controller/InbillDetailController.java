package com.ruoyi.wms.warehousing.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.dto.GroupDiskDto;
import com.ruoyi.wms.warehousing.dto.ReleaseTrayDto;
import com.ruoyi.wms.warehousing.service.InbillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 入库单详情信息Controller
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@RestController
@RequestMapping("/warehousing/detail")
public class InbillDetailController extends BaseController {

    @Autowired
    private InbillDetailService inbillDetailService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 查询入库单详情信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody InbillDetail inbillDetail) {
        logger.info("/warehousing/detail/list");
        startPage();
        List<InbillDetail> list = inbillDetailService.selectInbillDetailList(inbillDetail);
        return getDataTable(list);
    }

    /**
     * PDA-智能取盘列表
     */
    @PostMapping("/aiTakeTrayList")
    public TableDataInfo aiTakeTrayList(@RequestBody InbillDetail inbillDetail) {
        logger.info("/warehousing/detail/aiTakeTrayList");
        startPage();
        List<InbillDetail> list = inbillDetailService.aiTakeTrayList(inbillDetail);
        return getDataTable(list);
    }

    /**
     * 导出入库单详情信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:export')")
    @Log(title = "入库单详情信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InbillDetail inbillDetail) {
        logger.info("/warehousing/detail/export");
        List<InbillDetail> list = inbillDetailService.selectInbillDetailList(inbillDetail);
        ExcelUtil<InbillDetail> util = new ExcelUtil<InbillDetail>(InbillDetail.class);
        util.exportExcel(response, list, "入库单详情信息数据");
    }

    /**
     * 导入入库单详情信息列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:import')")
    @Log(title = "入库单详情信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        logger.info("/warehousing/detail/import");
        ExcelUtil<InbillDetail> util = new ExcelUtil<InbillDetail>(InbillDetail.class);
        List<InbillDetail> inbillDetailList = util.importExcel(file.getInputStream());
        return AjaxResult.success(inbillDetailList);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<InbillDetail> util = new ExcelUtil<InbillDetail>(InbillDetail.class);
        util.importTemplateExcel(response, "模板数据");
    }

    /**
     * 获取入库单详情信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/warehousing/detail/getInfo/id");
        return success(inbillDetailService.selectInbillDetailById(id));
    }

    /**
     * 新增入库单详情信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:add')")
    @Log(title = "入库单详情信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody InbillDetail inbillDetail) {
        logger.info("/warehousing/detail/add");
        return AjaxResult.success(inbillDetailService.insertInbillDetail(inbillDetail));
    }

    /**
     * 修改入库单详情信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:edit')")
    @Log(title = "入库单详情信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody InbillDetail inbillDetail) {
        logger.info("/warehousing/detail/edit");
        return AjaxResult.success(inbillDetailService.updateInbillDetail(inbillDetail));
    }

    /**
     * 删除入库单详情信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:detail:remove')")
    @Log(title = "入库单详情信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/warehousing/detail/remove/id");
        return AjaxResult.success(inbillDetailService.deleteInbillDetailByIds(ids));
    }

    /**
     * 验收
     */
    @Log(title = "入库单详情信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/checkGoods")
    public AjaxResult checkGoods(@RequestBody InBill inBill) {
        logger.info("/warehousing/detail/checkGoods");
        AjaxResult result = inbillDetailService.checkGoods(inBill);
        //清理入库单号下唯一码的流水号缓存
        redisCache.deleteObject(inBill.getInBillCode());
        return result;
    }
    /**
     * pda_组盘 理货区库位
     */
    @PostMapping(value = "/getLocation/{trayCode}")
    public AjaxResult getLocation(@PathVariable("trayCode") String trayCode) {
        logger.info("/warehousing/detail/getLocation/"+trayCode);
        return AjaxResult.success(inbillDetailService.getLocation(trayCode));
    }

    /**
     * 组盘
     */
    @Log(title = "入库单详情信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/groupDisk")
    public AjaxResult groupDisk(@RequestBody GroupDiskDto map) {
        logger.info("/warehousing/detail/groupDisk");
        return inbillDetailService.groupDisk(map);
    }

    /**
     * 组盘校验
     */
    @Log(title = "入库组盘 校验", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/groupDiskValidate")
    public AjaxResult groupDiskValidate(@RequestBody GroupDiskDto map) {
        logger.info("/warehousing/detail/groupDiskValidate");
        return inbillDetailService.groupDiskValidate(map);
    }

    /**
     * 解盘
     */
    @Log(title = "解盘", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/releaseTray")
    public AjaxResult releaseTray(@RequestBody ReleaseTrayDto dto) {
        logger.info("/warehousing/detail/releaseTray");
        return inbillDetailService.releaseTray(dto);
    }

    /**
     * 拉取离线数据
     */
    @Log(title = "拉取离线数据", businessType = BusinessType.UPDATE)
    @GetMapping(value = "/pullData")
    public AjaxResult pullData() {
        logger.info("/warehousing/detail/pullData");
        return inbillDetailService.pullData();
    }


    /**
     * 获取已取托盘
     * @param type  操作类型(1.智能取盘  2.人工取盘)
     */
    @Log(title = "获取已取托盘", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getHaveTakenTrayInfo/{type}")
    public AjaxResult pullData(@PathVariable("type") String type,String billNo) {
        logger.info("/warehousing/detail/getHaveTakenTrayInfo/" + type);
        return AjaxResult.success(inbillDetailService.getHaveTakenTrayInfo(billNo,type));
    }

}
