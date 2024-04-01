package com.ruoyi.wms.check.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckDetail;
import com.ruoyi.wms.check.domain.CheckGoods;
import com.ruoyi.wms.check.dto.CheckDetailVo;
import com.ruoyi.wms.check.service.WmsWarehouseCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 库存盘点Controller
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@RestController
@RequestMapping("/check")
public class WmsWarehouseCheckController extends BaseController {

    @Autowired
    private WmsWarehouseCheckService wmsWarehouseCheckService;

    /**
     * 查询库存盘点列表
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Check wmsWarehouseCheck) {
        logger.info("/check/list");
        startPage();
        List<Check> list = wmsWarehouseCheckService.selectWmsWarehouseCheckList(wmsWarehouseCheck);
        return getDataTable(list);
    }

    /**
     * 查询库位列表(库位)
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/getLocationList")
    public TableDataInfo getLocationList(@RequestBody Location location) {
        logger.info("/check/getLocationList");
        List<CheckDetailVo> list = wmsWarehouseCheckService.getLocationList(location);
        return getDataTable(list);
    }

    /**
     * 获取全盘详细数据信息(全盘)
     * @return
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @PostMapping(value = "/getAllCheckbill")
    public AjaxResult getAllCheckbill(){
        logger.info("/check/getAllCheckbill");
        List<CheckDetail> list = wmsWarehouseCheckService.getAllCheckbill();
        return AjaxResult.success(list);
    }

    /**
     * 查询货物类型列表(货物类型)
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/getGoodsList")
    public TableDataInfo getGoodsList(@RequestBody CheckDetail checkDetail) {
        logger.info("/check/getGoodsList");
        startPage();
        return getDataTable(wmsWarehouseCheckService.getGoodsList(checkDetail));
    }

    /**
     * 查询货物详情列表
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/getCheckGoodsList")
    public TableDataInfo getCheckGoodsList(@RequestBody CheckGoods checkGoods) {
        logger.info("/check/getCheckGoodsList");
        startPage();
        return getDataTable(wmsWarehouseCheckService.getCheckGoodsList(checkGoods));
    }

    /**
     * 查询库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/getCheckDetailInfo")
    public TableDataInfo getCheckDetailInfo(@RequestBody CheckDetail checkDetail) {
        logger.info("/check/getCheckDetailInfo");
        startPage();
        return getDataTable(wmsWarehouseCheckService.getCheckDetail(checkDetail));
    }
    /**
     * 查询库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('check:list')")
    @PostMapping("/getDetailListData")
    public TableDataInfo getDetailListData(@RequestBody Check check) {
        logger.info("/check/getDetailListData");
        startPage();
        return getDataTable(wmsWarehouseCheckService.getDetailListData(check));
    }

    /**
     * 获取库存盘点详情信息
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @GetMapping(value = "/getCheckDetailData/{id}")
    public AjaxResult getCheckDetailData(@PathVariable("id") String id){
        logger.info("/check/getCheckDetailData/id");
        return success(wmsWarehouseCheckService.getCheckDetailData(id));
    }
    /**
     * 获取存储区库区编码
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @PostMapping(value = "/getAreaCode")
    public AjaxResult getAreaCode(){
        logger.info("/check/getAreaCode");
        return success(wmsWarehouseCheckService.getAreaCode());
    }
    /**
     * 获取库存盘点详情信息
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @PostMapping(value = "/getLocationData")
    public AjaxResult getLocationData(){
        logger.info("/check/getLocationData");
        return success(wmsWarehouseCheckService.getLocationData());
    }

    /**
     * 获取库存盘点修改信息
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @GetMapping(value = "/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable("id") String checkBillCode){
        logger.info("/check/getInfo/id");
        return success(wmsWarehouseCheckService.selectWmsWarehouseCheckById(checkBillCode));
    }
    /**
     * 获取库存盘点打印数据
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @GetMapping(value = "/getPrintData/{id}")
    public AjaxResult getPrintData(@PathVariable("id") String id){
        logger.info("/check/getInfo/id");
        return success(wmsWarehouseCheckService.getPrintData(id));
    }

    /**
     * 新增库存盘点(全盘)
     */
    @PreAuthorize("@ss.hasPermi('check:add')")
    @Log(title = "库存盘点", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addAllCheck")
    public AjaxResult addAllCheck(@RequestBody CheckDetail checkDetail){
        logger.info("/check/addAllCheck");
        return AjaxResult.success(wmsWarehouseCheckService.insertWmsWarehouseCheck(checkDetail));
    }

    /**
     * 新增库存盘点(按库位盘)
     */
    @PreAuthorize("@ss.hasPermi('check:add')")
    @Log(title = "库存盘点", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addLocationCheck")
    public AjaxResult addLocationCheck(@RequestBody CheckDetail checkDetail){
        logger.info("/check/addLocationCheck");
        return AjaxResult.success(wmsWarehouseCheckService.insertLocationCheck(checkDetail));
    }

    /**
     * 新增库存盘点(按货物类型盘)
     */
    @PreAuthorize("@ss.hasPermi('check:add')")
    @Log(title = "库存盘点", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addGoodsTypeCheck")
    public AjaxResult addGoodsTypeCheck(@RequestBody CheckDetail checkDetail){
        logger.info("/check/addGoodsTypeCheck");
        return AjaxResult.success(wmsWarehouseCheckService.insertGoodsTypeCheck(checkDetail));
    }

    /**
     * 修改库存盘点
     */
    @PreAuthorize("@ss.hasPermi('check:edit')")
    @Log(title = "库存盘点", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/edit")
    public AjaxResult edit(@RequestBody Check wmsWarehouseCheck){
        if(StringUtils.isEmpty(wmsWarehouseCheck.getId())){
            return AjaxResult.error("修改失败，数据主键id缺失");
        }
        logger.info("/check/edit");
        return AjaxResult.success(wmsWarehouseCheckService.updateWmsWarehouseCheck(wmsWarehouseCheck));
    }

    /**
     * 删除库存盘点
     */
    @PreAuthorize("@ss.hasPermi('check:remove')")
    @Log(title = "库存盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable String[] ids){
        logger.info("/check/remove/id");
        return AjaxResult.success(wmsWarehouseCheckService.deleteWmsWarehouseCheckByIds(ids));
    }

    /**
     * 删除库存盘点详情
     */
    @PreAuthorize("@ss.hasPermi('check:remove')")
    @Log(title = "库存盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/delCheckDetail/{ids}")
    public AjaxResult delCheckDetail(@PathVariable String[] ids){
        logger.info("/check/delCheckDetail");
        return AjaxResult.success(wmsWarehouseCheckService.delCheckDetail(ids));
    }

    /**
     * 删除库存盘点配置
     */
    @PreAuthorize("@ss.hasPermi('check:remove')")
    @Log(title = "库存盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/delCheckConfig/{ids}")
    public AjaxResult delCheckConfig(@PathVariable String[] ids){
        logger.info("/check/delCheckConfig");
        return AjaxResult.success(wmsWarehouseCheckService.delCheckConfig(ids));
    }

    /**
     * 获取出库单打印信息
     */
    @PreAuthorize("@ss.hasPermi('check:query')")
    @Log(title = "出库单信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/printData")
    public void printData(@RequestBody Map map, HttpServletResponse responseBody) {
        logger.info("/check/printData");
        wmsWarehouseCheckService.printData(map,responseBody);
    }

}
