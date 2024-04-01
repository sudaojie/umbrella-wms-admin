package com.ruoyi.wms.warehousing.controller;

import com.google.zxing.WriterException;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import com.ruoyi.wms.warehousing.dto.InbillGoodsDto;
import com.ruoyi.wms.warehousing.service.InbillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * 入库单货物Controller
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@RestController
@RequestMapping("/warehousing/goods")
public class InbillGoodsController extends BaseController {

    @Autowired
    private InbillGoodsService inbillGoodsService;

    /**
     * 查询入库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/list");
        startPage();
        List<InbillGoods> list = inbillGoodsService.selectInbillGoodsList(inbillGoods);
        return getDataTable(list);
    }

    /**
     * 导出入库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:export')")
    @Log(title = "入库单货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/export");
        List<InbillGoods> list = inbillGoodsService.selectInbillGoodsList(inbillGoods);
        ExcelUtil<InbillGoods> util = new ExcelUtil<InbillGoods>(InbillGoods.class);
        util.exportExcel(response, list, "入库单货物数据");
    }

    /**
     * 获取入库单货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        logger.info("/warehousing/goods/getInfo/id");
        return success(inbillGoodsService.selectInbillGoodsById(id));
    }

    /**
     * 新增入库单货物
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:add')")
    @Log(title = "入库单货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/add");
        return AjaxResult.success(inbillGoodsService.insertInbillGoods(inbillGoods));
    }

    /**
     * 修改入库单货物
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:edit')")
    @Log(title = "入库单货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/edit");
        return AjaxResult.success(inbillGoodsService.updateInbillGoods(inbillGoods));
    }

    /**
     * 删除入库单货物
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:remove')")
    @Log(title = "入库单货物", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        logger.info("/warehousing/goods/remove/id");
        return AjaxResult.success(inbillGoodsService.deleteInbillGoodsByIds(ids));
    }
    /**
     * 生成二维码打印数据
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:printer')")
    @Log(title = "生成二维码数据", businessType = BusinessType.OTHER)
    @PostMapping(value = "/getPrintData")
    public AjaxResult getPrintData(@RequestBody InbillGoods inbillGoods) throws IOException, WriterException {
        logger.info("/basics/goods/getPrintData");
        return AjaxResult.success(inbillGoodsService.getPrintData(inbillGoods));
    }
    /**
     * 修改打印状态
     */
    @PreAuthorize("@ss.hasPermi('warehousing:goods:updateStatus')")
    @Log(title = "生成二维码数据", businessType = BusinessType.OTHER)
    @PostMapping(value = "/updateStatus")
    public AjaxResult updateStatus(@RequestBody InbillGoods inbillGoods)  {
        logger.info("/basics/goods/getPrintData");
        return AjaxResult.success(inbillGoodsService.updateStatus(inbillGoods));
    }

    /**
     * pda-机件号  获取入库单 下拉框
     */
    @Log(title = "入库单下拉框")
    @GetMapping(value = "/getInbillInfo")
    public AjaxResult getInbillInfo() {
        logger.info("/warehousing/goods/getInbillInfo");
        return AjaxResult.success(inbillGoodsService.getInbillInfo());
    }

    /**
     * pda-机件号  根据入库单号获取入库单详情下拉框
     */
    @Log(title = "入库单详情下拉框")
    @GetMapping(value = "/getInbillDetail/{inbilCode}")
    public AjaxResult getInbillDetail(@PathVariable("inbilCode")String inbilCode) {
        logger.info("/warehousing/goods/getInbillDetail/"+inbilCode);
        return AjaxResult.success(inbillGoodsService.getInbillDetail(inbilCode));
    }

    /**
     * pda-机件号 根据入库单详情id获取入库单货物数量
     */
    @Log(title = "货物数量")
    @GetMapping(value = "/getInBillNum/{inbilDetailId}")
    public AjaxResult getInBillNum(@PathVariable("inbilDetailId")String inbilDetailId) {
        logger.info("/warehousing/goods/getInBillNum/"+inbilDetailId);
        return AjaxResult.success(inbillGoodsService.getInBillNum(inbilDetailId));
    }

    /**
     * pda-机件号 保存机件号
     */
    @Log(title = "保存机件号")
    @PostMapping(value = "/saveInbillGoods")
    public AjaxResult saveInbillGoods(@RequestBody InbillGoodsDto inbillGoodsDto){
        logger.info("/warehousing/goods/saveInbillGoods");
        return AjaxResult.success(inbillGoodsService.saveInbillGoods(inbillGoodsDto));
    }

    /**
     * pda-根据条件去查询列表
     * @param inbillGoods 参数
     * @return 数据
     */
    @PostMapping("/getInbillGoodsInfo")
    public TableDataInfo getInbillGoodsInfo(@RequestBody InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/getInbillGoodsInfo");
        startPage();
        List<InbillGoods> list = inbillGoodsService.getInbillGoodsInfo(inbillGoods);
        return getDataTable(list);
    }

    /**
     * paa-打印机件号二维码
     */
    @Log(title = "打印机件号二维码", businessType = BusinessType.OTHER)
    @PostMapping(value = "/printGoods")
    public AjaxResult printGoods(@RequestBody Parts parts) throws Exception {
        logger.info("/warehousing/goods/printGoods");
        AjaxResult ajaxResult = inbillGoodsService.printGoods(parts);
        return  ajaxResult;
    }

    /**
     * paa-根据id查询详细信息
     */
    @GetMapping(value = "/getGoodsById/{id}")
    public AjaxResult getGoodsById(@PathVariable("id") String id) {
        logger.info("/warehousing/goods/getGoodsById/"+id);
        return  AjaxResult.success(inbillGoodsService.getGoodsById(id));
    }

    /**
     * paa-编辑机件号
     */
    @PostMapping(value = "/updateGoods")
    public AjaxResult updateGoods(@RequestBody InbillGoods inbillGoods) {
        logger.info("/warehousing/goods/updateGoods");
        return  AjaxResult.success(inbillGoodsService.updateGoods(inbillGoods));
    }

}
