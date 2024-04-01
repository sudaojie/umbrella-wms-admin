package com.ruoyi.wms.group.disk.data.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskDataInfo;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskGoodsInfo;
import com.ruoyi.wms.group.disk.data.domain.vo.WmsGroupDiskDataInfoVO;
import com.ruoyi.wms.group.disk.data.service.WmsGroupDiskDataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * wms已组盘数据信息Controller
 *
 * @author hewei
 * @date 2023-04-19
 */
@RestController
@RequestMapping("/wms/groupDiskData")
public class WmsGroupDiskDataInfoController extends BaseController {

    @Autowired
    private WmsGroupDiskDataInfoService wmsGroupDiskDataInfoService;

    /**
     * 查询wms已组盘数据信息列表
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        logger.info("/wms/groupDiskData/list");
        startPage();
        List<WmsGroupDiskDataInfo> list = wmsGroupDiskDataInfoService.selectWmsGroupDiskDataInfoList(wmsGroupDiskDataInfo);
        return getDataTable(list);
    }

    /**
     * 查询wms已组盘托盘上货物信息列表
     */
    @PostMapping("/getGoodsInfoOnGroupTray")
    public TableDataInfo getGoodsInfoOnGroupTray(@RequestBody WmsGroupDiskGoodsInfo wmsGroupDiskGoodsInfo) {
        logger.info("/wms/groupDiskData/getGoodsInfoOnGroupTray");
        startPage();
        List<WmsGroupDiskGoodsInfo> list = wmsGroupDiskDataInfoService.getGoodsInfoOnGroupTray(wmsGroupDiskGoodsInfo);
        return getDataTable(list);
    }

    /**
     * 查询wms PDA 已组盘数据信息列表
     */
    @PostMapping("/groupList")
    public TableDataInfo groupList(@RequestBody WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        logger.info("/wms/groupDiskData/groupList");
        startPage();
        List<WmsGroupDiskDataInfoVO> list = wmsGroupDiskDataInfoService.selectWmsGroupDiskDataInfoListPda(wmsGroupDiskDataInfo);
        return getDataTable(list);
    }

    /**
     * 查询 PDA 未组盘数据信息列表
     */
    @PostMapping("/unGroupDiskGoodsList")
    public TableDataInfo unGroupDiskGoodsList(@RequestBody WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        logger.info("/wms/groupDiskData/unGroupDiskGoodsList");
        startPage();
        return getDataTable(wmsGroupDiskDataInfoService.selectUnGroupDiskGoodsList(wmsGroupDiskDataInfo));
    }



}
