package com.ruoyi.wcs.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskRunStatusEnum;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.enums.DryInbillStatusEnum;
import com.ruoyi.wms.enums.ListingEnum;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.mapper.OutbillGoodsMapper;
import com.ruoyi.wms.outbound.service.OutbillGoodsService;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import com.ruoyi.wms.stock.mapper.DryOutbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillMapper;
import com.ruoyi.wms.stock.service.DryOutbillGoodsService;
import com.ruoyi.wms.stock.service.TblstockService;
import com.ruoyi.wms.stock.service.WmsDryInbillGoodsService;
import com.ruoyi.wms.stock.service.WmsDryInbillService;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.mapper.ListingDetailMapper;
import com.ruoyi.wms.warehousing.service.ListingDetailService;
import com.ruoyi.wms.wcstask.domain.WmsWcsCallbackInfo;
import com.ruoyi.wms.wcstask.service.WmsWcsCallbackInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WCS任务信息Service接口
 *
 * @author yangjie
 * @date 2023-02-28
 */
@Slf4j
@Service
public class WcsOperateTaskService extends ServiceImpl<WcsOperateTaskMapper, WcsOperateTask> {

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Value("${stacker.remote.api}")
    private String stackerRemoteApiUrl;

    @Value("${agv.remote.api}")
    private String agvRemoteApiUrl;

    @Autowired
    private ListingDetailMapper listingDetailMapper;

    @Autowired
    private ListingDetailService listingDetailService;

    @Autowired
    private OutbillGoodsMapper outbillGoodsMapper;

    @Autowired
    private OutbillGoodsService outbillGoodsService;

    @Autowired
    private DryOutbillGoodsMapper dryOutbillGoodsMapper;

    @Autowired
    private DryOutbillGoodsService dryOutbillGoodsService;

    @Autowired
    private TblstockMapper tblstockMapper;

    @Autowired
    private TblstockService tblstockService;

    @Autowired
    private WmsDryInbillMapper wmsDryInbillMapper;

    @Autowired
    private WmsDryInbillService wmsDryInbillService;

    @Autowired
    private WmsDryInbillGoodsMapper wmsDryInbillGoodsMapper;

    @Autowired
    private WmsDryInbillGoodsService wmsDryInbillGoodsService;



    /**
     * 查询WCS任务信息
     *
     * @param id WCS任务信息主键
     * @return WCS任务信息
     */
    public WcsOperateTask selectWcsOperateTaskById(String id) {
        QueryWrapper<WcsOperateTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wcsOperateTaskMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询WCS任务信息
     *
     * @param ids WCS任务信息 IDs
     * @return WCS任务信息
     */
    public List<WcsOperateTask> selectWcsOperateTaskByIds(String[] ids) {
        QueryWrapper<WcsOperateTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wcsOperateTaskMapper.selectList(queryWrapper);
    }

    /**
     * 查询WCS任务信息列表
     *
     * @param wcsOperateTask WCS任务信息
     * @return WCS任务信息集合
     */
    public List<WcsOperateTask> selectWcsOperateTaskList(WcsOperateTask wcsOperateTask) {
        QueryWrapper<WcsOperateTask> queryWrapper = getQueryWrapper(wcsOperateTask);
        return wcsOperateTaskMapper.select(queryWrapper);
    }

    /**
     * 新增WCS任务信息
     *
     * @param wcsOperateTask WCS任务信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsOperateTask insertWcsOperateTask(WcsOperateTask wcsOperateTask) {
        wcsOperateTask.setId(IdUtil.simpleUUID());
        wcsOperateTask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wcsOperateTaskMapper.insert(wcsOperateTask);
        return wcsOperateTask;
    }

    /**
     * 修改WCS任务信息
     *
     * @param wcsOperateTask WCS任务信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WcsOperateTask updateWcsOperateTask(WcsOperateTask wcsOperateTask) {
        wcsOperateTaskMapper.updateById(wcsOperateTask);
        return wcsOperateTask;
    }

    /**
     * 批量删除WCS任务信息
     *
     * @param ids 需要删除的WCS任务信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsOperateTaskByIds(String[] ids) {
        List<WcsOperateTask> wcsOperateTasks = new ArrayList<>();
        List<String> batchIds = new ArrayList<>();
        for (String id : ids) {
            WcsOperateTask wcsOperateTask = new WcsOperateTask();
            wcsOperateTask.setId(id);
            wcsOperateTask.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wcsOperateTasks.add(wcsOperateTask);
            batchIds.add(id);
        }

        List<WcsOperateTask> wcsAllOperateTasks = wcsOperateTaskMapper.selectBatchIds(batchIds);

        //AGV任务
        List<WcsOperateTask> agvTask = wcsAllOperateTasks.stream().filter(item -> item.getTaskDeviceType().equals("1")).collect(Collectors.toList());
        //堆垛机任务
        List<WcsOperateTask> stackerTask = wcsAllOperateTasks.stream().filter(item -> item.getTaskDeviceType().equals("2")).collect(Collectors.toList());

        if(CollUtil.isNotEmpty(agvTask)){
            for (WcsOperateTask wcsOperateTask : agvTask) {
                String url = agvRemoteApiUrl + "/StopTask/" + wcsOperateTask.getTaskNo();
                log.info("调用AGV WCS系统的删除任务接口,url:{}",url);
                String response = HttpUtils.sendGet(url);
                log.info("调用AGV WCS系统的删除任务接口,响应:{}",response);

                String startPosition = wcsOperateTask.getStartPosition();
                String endPosition = wcsOperateTask.getEndPosition();
                UpdateWrapper<Location> locationUpdateWrapper = new UpdateWrapper<>();
                UpdateWrapper<Location> locationUpdateWrapper1 = new UpdateWrapper<>();
                if(WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())){
                    //取盘
                    locationUpdateWrapper1.set("tray_code",null);
                }else if(WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())){
                    //回盘
                    locationUpdateWrapper.set("tray_code",wcsOperateTask.getTrayNo());
                }
                locationUpdateWrapper.eq("location_code",startPosition);
                locationUpdateWrapper.set("lock_status", LockEnum.NOTLOCK.getCode());
                locationService.update(locationUpdateWrapper);



                locationUpdateWrapper1.eq("location_code",endPosition);
                locationUpdateWrapper1.set("lock_status", LockEnum.NOTLOCK.getCode());
                locationService.update(locationUpdateWrapper1);

                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.FORCED_INTERRUPT.getCode());
                wcsOperateTaskMapper.updateById(wcsOperateTask);
            }
        }

        if (CollUtil.isNotEmpty(stackerTask)) {
            for (WcsOperateTask wcsOperateTask : stackerTask) {
                String url = stackerRemoteApiUrl + "/stackerTasks/deleteTask?taskNo=" + wcsOperateTask.getTaskNo();
                log.info("调用堆垛机 WCS系统的删除任务接口,url:{}",url);
                String response = HttpUtils.sendGet(url);
                log.info("调用堆垛机 WCS系统的删除任务接口,响应:{}",response);

                //正常入库
                if(WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(wcsOperateTask.getTaskType())
                        && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                        && WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())
                ){
                    //上架单详情对象
                    LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
                    listingDetailQuery.eq(ListingDetail::getInBillCode,wcsOperateTask.getInBillNo())
                            .eq(ListingDetail::getTrayCode,wcsOperateTask.getTrayNo())
                            .eq(ListingDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    List<ListingDetail> listingDetails = listingDetailMapper.selectList(listingDetailQuery);
                    if(listingDetails.size()>0){
                        //修改上架状态为未上架
                        List<String> listingDetailIds = listingDetails.stream().map(ListingDetail::getId).collect(Collectors.toList());
                        UpdateWrapper<ListingDetail> listingDetailUpdateWrapper = new UpdateWrapper<>();
                        listingDetailUpdateWrapper.in("id", listingDetailIds);
                        listingDetailUpdateWrapper.set("listing_status", ListingEnum.NOT.getCode());
                        listingDetailService.update(listingDetailUpdateWrapper);
                    }
                }else if(WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(wcsOperateTask.getTaskType())
                        && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                        && WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())
                ){
                    //正常出库
                    LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
                    outbillGoodsQuery.eq(OutbillGoods::getOutBillCode,wcsOperateTask.getInBillNo())
                            .eq(OutbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                            .eq(OutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    List<OutbillGoods> outbillGoods = outbillGoodsMapper.selectList(outbillGoodsQuery);
                    if(outbillGoods.size()>0){
                        //修改出库状态为待拣货
                        List<String> outbillGoodIds = outbillGoods.stream().map(OutbillGoods::getId).collect(Collectors.toList());
                        UpdateWrapper<OutbillGoods> outbillGoodUpdateWrapper = new UpdateWrapper<>();
                        outbillGoodUpdateWrapper.in("id", outbillGoodIds);
                        outbillGoodUpdateWrapper.set("out_bill_status", "0");
                        outbillGoodsService.update(outbillGoodUpdateWrapper);
                    }
                }else if(WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode().equals(wcsOperateTask.getTaskType())
                        && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                        && WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())
                ){
                    //晾晒出库
                    LambdaQueryWrapper<DryOutbillGoods> dryOutbillbillQuery = Wrappers.lambdaQuery();
                    dryOutbillbillQuery.eq(DryOutbillGoods::getDryOutbillCode,wcsOperateTask.getInBillNo())
                            .eq(DryOutbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                            .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    List<DryOutbillGoods> dryOutbillGoods = dryOutbillGoodsMapper.selectList(dryOutbillbillQuery);
                    if(dryOutbillGoods.size()>0){
                        //修改出库状态为待出库
                        List<String> dryOutbillGoodIds = dryOutbillGoods.stream().map(DryOutbillGoods::getId).collect(Collectors.toList());
                        UpdateWrapper<DryOutbillGoods> dryOutbillGoodUpdateWrapper = new UpdateWrapper<>();
                        dryOutbillGoodUpdateWrapper.in("id", dryOutbillGoodIds);
                        dryOutbillGoodUpdateWrapper.set("dry_outbill_status", "0");
                        dryOutbillGoodsService.update(dryOutbillGoodUpdateWrapper);

                        //修改库存台账锁定状态为未锁定
                        List<String> dryOutbillGoodTrayCodes = dryOutbillGoods.stream().map(DryOutbillGoods::getTrayCode).collect(Collectors.toList());
                        UpdateWrapper<Tblstock> tblstockUpdateWrapper = new UpdateWrapper<>();
                        tblstockUpdateWrapper.in("tray_code", dryOutbillGoodTrayCodes);
                        tblstockUpdateWrapper.set("lock_status", "0");
                        tblstockService.update(tblstockUpdateWrapper);

                    }
                }else if(WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(wcsOperateTask.getTaskType())
                        && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                        && WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())
                ){
                    //晾晒入库
                    LambdaQueryWrapper<WmsDryInbillGoods> wmsDryInbillGoodsQuery = Wrappers.lambdaQuery();
                    wmsDryInbillGoodsQuery.eq(WmsDryInbillGoods::getDryInbillCode,wcsOperateTask.getInBillNo())
                            .eq(WmsDryInbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                            .eq(WmsDryInbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    List<WmsDryInbillGoods> wmsDryInbillGoods = wmsDryInbillGoodsMapper.selectList(wmsDryInbillGoodsQuery);
                    if(wmsDryInbillGoods.size()>0){
                        //修改晾晒入库状态为已组盘
                        List<String> dryInbillGoods = wmsDryInbillGoods.stream().map(WmsDryInbillGoods::getId).collect(Collectors.toList());
                        UpdateWrapper<WmsDryInbillGoods> wmsDryInbillGoodUpdateWrapper = new UpdateWrapper<>();
                        wmsDryInbillGoodUpdateWrapper.in("id", dryInbillGoods);
                        wmsDryInbillGoodUpdateWrapper.set("dry_inbill_status", DryInbillStatusEnum.TAKE.getCode());
                        wmsDryInbillGoodsService.update(wmsDryInbillGoodUpdateWrapper);

                        //修改晾晒入库状态为已组盘
                        UpdateWrapper<WmsDryInbill> wmsDryInbillUpdateWrapper = new UpdateWrapper<>();
                        wmsDryInbillUpdateWrapper.eq("dry_inbill_code", wcsOperateTask.getInBillNo());
                        wmsDryInbillUpdateWrapper.set("dry_inbill_status", DryInbillStatusEnum.TAKE.getCode());
                        wmsDryInbillService.update(wmsDryInbillUpdateWrapper);

                    }
                }

                String startPosition = wcsOperateTask.getStartPosition();
                String endPosition = wcsOperateTask.getEndPosition();
                UpdateWrapper<Location> locationUpdateWrapper = new UpdateWrapper<>();
                locationUpdateWrapper.eq("location_code",startPosition);
                locationUpdateWrapper.set("lock_status", LockEnum.NOTLOCK.getCode());
                locationService.update(locationUpdateWrapper);


                UpdateWrapper<Location> locationUpdateWrapper1 = new UpdateWrapper<>();
                locationUpdateWrapper1.eq("location_code",endPosition);
                locationUpdateWrapper1.set("lock_status", LockEnum.NOTLOCK.getCode());
                locationService.update(locationUpdateWrapper1);


                wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.FORCED_INTERRUPT.getCode());
                wcsOperateTaskMapper.updateById(wcsOperateTask);
            }
        }
        //return super.updateBatchById(wcsOperateTasks) ? 1 : 0;
        return 1;
    }

    /**
     * 删除WCS任务信息信息
     *
     * @param id WCS任务信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWcsOperateTaskById(String id) {
        WcsOperateTask wcsOperateTask = new WcsOperateTask();
        wcsOperateTask.setId(id);
        wcsOperateTask.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wcsOperateTaskMapper.updateById(wcsOperateTask);
    }

    public QueryWrapper<WcsOperateTask> getQueryWrapper(WcsOperateTask wcsOperateTask) {
        QueryWrapper<WcsOperateTask> queryWrapper = new QueryWrapper<>();
        if (wcsOperateTask != null) {
            wcsOperateTask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wcsOperateTask.getDelFlag());
            //任务号
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskNo())) {
                queryWrapper.like("task_no", wcsOperateTask.getTaskNo());
            }
            //单据号
            if (StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())) {
                queryWrapper.like("in_bill_no", wcsOperateTask.getInBillNo());
            }
            //托盘号
            if (StrUtil.isNotEmpty(wcsOperateTask.getTrayNo())) {
                queryWrapper.like("tray_no", wcsOperateTask.getTrayNo());
            }
            //操作类型
            if (StrUtil.isNotEmpty(wcsOperateTask.getOperateType())) {
                queryWrapper.like("operate_type", wcsOperateTask.getOperateType());
            }
            //任务设备类型(1.AVG 2.堆垛机)
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskDeviceType())) {
                queryWrapper.eq("task_device_type", wcsOperateTask.getTaskDeviceType());
            }
            //任务类型
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskType())) {
                queryWrapper.eq("task_type", wcsOperateTask.getTaskType());
            }
            //起始位置
            if (StrUtil.isNotEmpty(wcsOperateTask.getStartPosition())) {
                queryWrapper.eq("start_position", wcsOperateTask.getStartPosition());
            }
            //目标位置
            if (StrUtil.isNotEmpty(wcsOperateTask.getEndPosition())) {
                queryWrapper.eq("end_position", wcsOperateTask.getEndPosition());
            }
            //执行开始时间
            if (wcsOperateTask.getOperateBeginTime() != null) {
                queryWrapper.eq("operate_begin_time", wcsOperateTask.getOperateBeginTime());
            }
            //执行结束时间
            if (wcsOperateTask.getOperateEndTime() != null) {
                queryWrapper.eq("operate_end_time", wcsOperateTask.getOperateEndTime());
            }
            //任务请求json
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskReqJson())) {
                queryWrapper.eq("task_req_json", wcsOperateTask.getTaskReqJson());
            }
            //任务响应json
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskRspJson())) {
                queryWrapper.eq("task_rsp_json", wcsOperateTask.getTaskRspJson());
            }
            //任务状态(1.未执行  2.执行中  3.执行成功 4.执行失败  5.人工中断)
            if (StrUtil.isNotEmpty(wcsOperateTask.getTaskStatus())) {
                queryWrapper.eq("task_status", wcsOperateTask.getTaskStatus());
            }
        }
        return queryWrapper;
    }

    @Autowired
    private WmsWcsCallbackInfoService wmsWcsCallbackInfoService;

    @Autowired
    private LocationService locationService;

    /**
     * 强制手动完成堆垛机WCS任务
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean forceCompleteStacker(String id) {
        WcsOperateTask wcsOperateTask = this.selectWcsOperateTaskById(id);
        if (wcsOperateTask == null) {
            throw new ServiceException("该WCS任务记录不存在");
        }


        WmsWcsCallbackInfo wmsWcsCallbackInfo = wmsWcsCallbackInfoService.getBaseMapper().selectOne(
                new QueryWrapper<WmsWcsCallbackInfo>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("task_no", wcsOperateTask.getTaskNo()));
        if (wmsWcsCallbackInfo == null) {
            throw new ServiceException("回调队列任务，记录不存在或已被消费,任务号：" + wcsOperateTask.getTaskNo());
        }

        WmsWcsInfo wmsWcsInfo = new WmsWcsInfo();
        wmsWcsInfo.put(WmsWcsInfo.TYPE, wmsWcsCallbackInfo.getType());
        wmsWcsInfo.put(WmsWcsInfo.TASKTYPE, wmsWcsCallbackInfo.getTaskType());
        wmsWcsInfo.put(WmsWcsInfo.AREATYPE, wmsWcsCallbackInfo.getAreaType());
        wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, wmsWcsCallbackInfo.getTrayCode());
        wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, wmsWcsCallbackInfo.getStartAreaCode());
        wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, wmsWcsCallbackInfo.getEndAreaCode());
        wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, wmsWcsCallbackInfo.getStartLocationCode());
        wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, wmsWcsCallbackInfo.getEndLocationCode());
        wmsWcsInfo.put(WmsWcsInfo.DOC, wmsWcsCallbackInfo.getDoc());
        wmsWcsInfo.put(WmsWcsInfo.SERVICE_ID, wmsWcsCallbackInfo.getServiceId());
        wmsWcsInfo.put(WmsWcsInfo.MOVE_LAST, wmsWcsCallbackInfo.getMoveLast());

        //堆垛机到位消费信息
        locationService.stackerInfo(wmsWcsInfo);
        //将回调记录变成已删除,标识为已消费
        wmsWcsCallbackInfoService.deleteWmsWcsCallbackInfoById(wmsWcsCallbackInfo.getId());

        //将原来的任务强制变成执行成功
        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.COMPLETED.getCode());
        wcsOperateTaskMapper.updateById(wcsOperateTask);
        return true;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public boolean handleCancelStacker(String id) {
        WcsOperateTask wcsOperateTask = this.selectWcsOperateTaskById(id);
        if (wcsOperateTask == null) {
            throw new ServiceException("该WCS任务记录不存在");
        }
        //正常入库
        if(WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(wcsOperateTask.getTaskType())
                && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                && WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())
        ){
            //上架单详情对象
            LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
            listingDetailQuery.eq(ListingDetail::getInBillCode,wcsOperateTask.getInBillNo())
                    .eq(ListingDetail::getTrayCode,wcsOperateTask.getTrayNo())
                    .eq(ListingDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<ListingDetail> listingDetails = listingDetailMapper.selectList(listingDetailQuery);
            if(listingDetails.size()>0){
                //修改上架状态为未上架
                List<String> listingDetailIds = listingDetails.stream().map(ListingDetail::getId).collect(Collectors.toList());
                UpdateWrapper<ListingDetail> listingDetailUpdateWrapper = new UpdateWrapper<>();
                listingDetailUpdateWrapper.in("id", listingDetailIds);
                listingDetailUpdateWrapper.set("listing_status", ListingEnum.NOT.getCode());
                listingDetailService.update(listingDetailUpdateWrapper);
            }
        }else if(WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(wcsOperateTask.getTaskType())
                && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                && WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())
        ){
            //正常出库
            LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
            outbillGoodsQuery.eq(OutbillGoods::getOutBillCode,wcsOperateTask.getInBillNo())
                    .eq(OutbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                    .eq(OutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<OutbillGoods> outbillGoods = outbillGoodsMapper.selectList(outbillGoodsQuery);
            if(outbillGoods.size()>0){
                //修改出库状态为待拣货
                List<String> outbillGoodIds = outbillGoods.stream().map(OutbillGoods::getId).collect(Collectors.toList());
                UpdateWrapper<OutbillGoods> outbillGoodUpdateWrapper = new UpdateWrapper<>();
                outbillGoodUpdateWrapper.in("id", outbillGoodIds);
                outbillGoodUpdateWrapper.set("out_bill_status", "0");
                outbillGoodsService.update(outbillGoodUpdateWrapper);
            }
        }else if(WmsWcsTaskTypeEnum.DRYING_OUTBOUND.getCode().equals(wcsOperateTask.getTaskType())
                && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                && WmsWcsTypeEnum.TAKETRAY.getCode().equals(wcsOperateTask.getOperateType())
        ){
            //晾晒出库
            LambdaQueryWrapper<DryOutbillGoods> dryOutbillbillQuery = Wrappers.lambdaQuery();
            dryOutbillbillQuery.eq(DryOutbillGoods::getDryOutbillCode,wcsOperateTask.getInBillNo())
                    .eq(DryOutbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                    .eq(DryOutbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<DryOutbillGoods> dryOutbillGoods = dryOutbillGoodsMapper.selectList(dryOutbillbillQuery);
            if(dryOutbillGoods.size()>0){
                //修改出库状态为待出库
                List<String> dryOutbillGoodIds = dryOutbillGoods.stream().map(DryOutbillGoods::getId).collect(Collectors.toList());
                UpdateWrapper<DryOutbillGoods> dryOutbillGoodUpdateWrapper = new UpdateWrapper<>();
                dryOutbillGoodUpdateWrapper.in("id", dryOutbillGoodIds);
                dryOutbillGoodUpdateWrapper.set("dry_outbill_status", "0");
                dryOutbillGoodsService.update(dryOutbillGoodUpdateWrapper);

                //修改库存台账锁定状态为未锁定
                List<String> dryOutbillGoodTrayCodes = dryOutbillGoods.stream().map(DryOutbillGoods::getTrayCode).collect(Collectors.toList());
                UpdateWrapper<Tblstock> tblstockUpdateWrapper = new UpdateWrapper<>();
                tblstockUpdateWrapper.in("tray_code", dryOutbillGoodTrayCodes);
                tblstockUpdateWrapper.set("lock_status", "0");
                tblstockService.update(tblstockUpdateWrapper);

            }
        }else if(WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(wcsOperateTask.getTaskType())
                && StrUtil.isNotEmpty(wcsOperateTask.getInBillNo())
                && WmsWcsTypeEnum.PUTTRAY.getCode().equals(wcsOperateTask.getOperateType())
        ){
            //晾晒入库
            LambdaQueryWrapper<WmsDryInbillGoods> wmsDryInbillGoodsQuery = Wrappers.lambdaQuery();
            wmsDryInbillGoodsQuery.eq(WmsDryInbillGoods::getDryInbillCode,wcsOperateTask.getInBillNo())
                    .eq(WmsDryInbillGoods::getTrayCode,wcsOperateTask.getTrayNo())
                    .eq(WmsDryInbillGoods::getDelFlag,DelFlagEnum.DEL_NO.getCode());
            List<WmsDryInbillGoods> wmsDryInbillGoods = wmsDryInbillGoodsMapper.selectList(wmsDryInbillGoodsQuery);
            if(wmsDryInbillGoods.size()>0){
                //修改晾晒入库状态为已组盘
                List<String> dryInbillGoods = wmsDryInbillGoods.stream().map(WmsDryInbillGoods::getId).collect(Collectors.toList());
                UpdateWrapper<WmsDryInbillGoods> wmsDryInbillGoodUpdateWrapper = new UpdateWrapper<>();
                wmsDryInbillGoodUpdateWrapper.in("id", dryInbillGoods);
                wmsDryInbillGoodUpdateWrapper.set("dry_inbill_status", DryInbillStatusEnum.TAKE.getCode());
                wmsDryInbillGoodsService.update(wmsDryInbillGoodUpdateWrapper);

                //修改晾晒入库状态为已组盘
                UpdateWrapper<WmsDryInbill> wmsDryInbillUpdateWrapper = new UpdateWrapper<>();
                wmsDryInbillUpdateWrapper.eq("dry_inbill_code", wcsOperateTask.getInBillNo());
                wmsDryInbillUpdateWrapper.set("dry_inbill_status", DryInbillStatusEnum.TAKE.getCode());
                wmsDryInbillService.update(wmsDryInbillUpdateWrapper);

            }
        }

        String startPosition = wcsOperateTask.getStartPosition();
        String endPosition = wcsOperateTask.getEndPosition();

        UpdateWrapper<Location> locationUpdateWrapper = new UpdateWrapper<>();
        locationUpdateWrapper.eq("location_code",startPosition);
        locationUpdateWrapper.set("lock_status", LockEnum.NOTLOCK.getCode());
        locationService.update(locationUpdateWrapper);


        UpdateWrapper<Location> locationUpdateWrapper1 = new UpdateWrapper<>();
        locationUpdateWrapper1.eq("location_code",endPosition);
        locationUpdateWrapper1.set("lock_status", LockEnum.NOTLOCK.getCode());
        locationService.update(locationUpdateWrapper1);


        wcsOperateTask.setTaskStatus(WcsTaskRunStatusEnum.FORCED_INTERRUPT.getCode());
        wcsOperateTaskMapper.updateById(wcsOperateTask);
        return true;
    }
}
