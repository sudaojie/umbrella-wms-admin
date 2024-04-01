package com.ruoyi.wms.stock.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.stock.dto.DryInbillGroupDiskDto;
import com.ruoyi.wms.stock.dto.DryInbillPutOnDto;
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
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import com.ruoyi.wms.stock.service.WmsDryInbillGoodsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.page.TableDataInfo;


/**
 * 晾晒出入库单货物Controller
 *
 * @author nf
 * @date 2023-03-10
 */
@RestController
@RequestMapping("/stock/dryGoods")
public class WmsDryInbillGoodsController extends BaseController {

    @Autowired
    private WmsDryInbillGoodsService wmsDryInbillGoodsService;

    /**
     * 查询晾晒出入库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsDryInbillGoods wmsDryInbillGoods) {
        logger.info("/stock/dryGoods/list");
        startPage();
        List<WmsDryInbillGoods> list = wmsDryInbillGoodsService.selectWmsDryInbillGoodsList(wmsDryInbillGoods);
        return getDataTable(list);
    }

    /**
     * 导出晾晒出入库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:export')")
    @Log(title = "晾晒出入库单货物", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsDryInbillGoods wmsDryInbillGoods){
        logger.info("/stock/dryGoods/export");
        List<WmsDryInbillGoods> list = wmsDryInbillGoodsService.selectWmsDryInbillGoodsList(wmsDryInbillGoods);
        ExcelUtil<WmsDryInbillGoods> util = new ExcelUtil<WmsDryInbillGoods>(WmsDryInbillGoods.class);
        util.exportExcel(response, list, "晾晒出入库单货物数据");
    }
    /**
     * 导入晾晒出入库单货物列表
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:import')")
    @Log(title = "晾晒出入库单货物", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport)throws Exception{
        logger.info("/stock/dryGoods/import");
        ExcelUtil<WmsDryInbillGoods> util = new ExcelUtil<WmsDryInbillGoods>(WmsDryInbillGoods.class);
        List<WmsDryInbillGoods> wmsDryInbillGoodsList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = wmsDryInbillGoodsService.importData(wmsDryInbillGoodsList, updateSupport, operName);
        return AjaxResult.success(message);
    }
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsDryInbillGoods> util = new ExcelUtil<WmsDryInbillGoods>(WmsDryInbillGoods.class);
        util.importTemplateExcel(response,"模板数据");
    }
    /**
     * 获取晾晒出入库单货物详细信息
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id){
        logger.info("/stock/dryGoods/getInfo/id");
        return success(wmsDryInbillGoodsService.selectWmsDryInbillGoodsById(id));
    }

    /**
     * 新增晾晒出入库单货物
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:add')")
    @Log(title = "晾晒出入库单货物", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody WmsDryInbillGoods wmsDryInbillGoods){
        logger.info("/stock/dryGoods/add");
        return AjaxResult.success(wmsDryInbillGoodsService.insertWmsDryInbillGoods(wmsDryInbillGoods));
    }

    /**
     * 修改晾晒出入库单货物
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:edit')")
    @Log(title = "晾晒出入库单货物", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody WmsDryInbillGoods wmsDryInbillGoods){
        if(StringUtils.isEmpty(wmsDryInbillGoods.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        logger.info("/stock/dryGoods/edit");
        return AjaxResult.success(wmsDryInbillGoodsService.updateWmsDryInbillGoods(wmsDryInbillGoods));
    }

    /**
     * 删除晾晒出入库单货物
     */
    @PreAuthorize("@ss.hasPermi('stock:dryGoods:remove')")
    @Log(title = "晾晒出入库单货物", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/stock/dryGoods/remove/id");
        return AjaxResult.success(wmsDryInbillGoodsService.deleteWmsDryInbillGoodsByIds(ids));
    }

    /**
     * 晾晒入库组盘
     * @param dtoList
     * @return
     */
    @Log(title = "晾晒入库组盘", businessType = BusinessType.OTHER)
    @PostMapping("/groupDisk")
    public AjaxResult groupDisk(@RequestBody List<DryInbillGroupDiskDto> dtoList){
        logger.info("/stock/dryGoods/groupDisk");
        wmsDryInbillGoodsService.groupDisk(dtoList);
        return AjaxResult.success("成功");
    }

    /**
     * 晾晒入库上架
     * @param dto
     * @return
     */
    @Log(title = "晾晒入库上架", businessType = BusinessType.OTHER)
    @PostMapping("/putOn")
    public AjaxResult putOn(@RequestBody DryInbillPutOnDto dto){
        logger.info("/stock/dryGoods/putOn");
        wmsDryInbillGoodsService.putOn(dto);
        return AjaxResult.success("成功");
    }

    /**
     * 晾晒入库上架页面查看详情
     */
    @GetMapping(value = "/selectDryInbillGoods/{dryInbillCode}")
    public AjaxResult selectDryInbillGoods(@PathVariable("dryInbillCode") String dryInbillCode){
        logger.info("/stock/dryGoods/selectDryInbillGoods/dryInbillCode");
        return success(wmsDryInbillGoodsService.selectDryInbillGoods(dryInbillCode));
    }

}
