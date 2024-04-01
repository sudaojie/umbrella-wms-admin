package com.ruoyi.wms.gendata.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.wcs.constans.WcsConstants;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.dto.TrayDto;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.gendata.dto.GenDto;
import com.ruoyi.wms.global.WmsTaskConstant;
import com.ruoyi.wms.utils.SerialCodeUtils;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.dto.GroupDiskDto;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.warehousing.service.InbillDetailService;
import com.ruoyi.wms.warehousing.service.InbillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sdj
 * @create 2023-0
 * 6-17 17:29
 */
@Service
public class GenDataService {

    //入库单货物表
    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;

    //生成随机数
    @Autowired
    private SerialCodeUtils serialCodeUtils;

    //入库单货物表
    @Autowired
    private InbillGoodsService inbillGoodsService;

    //托盘基本信息Service接口
    @Autowired
    private TrayService trayService;

    //入库单详情信息Service接口
    @Autowired
    private InbillDetailService inbillDetailService;

    //库位基本信息
    @Autowired
    private LocationMapper locationMapper;

    //托盘基本信息
    @Autowired
    private TrayMapper trayMapper;

    //WCS设备基本信息Mapper接口
    @Autowired
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    //WCS任务信息Mapper接口
    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    //库区基本信息Mapper接口
    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private RedisCache redisCache;


    public AjaxResult boolAgv(){
        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode,"LHQ01")
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        return AjaxResult.success(wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery));
    }




    /**
     * 生成入库数据
     * @param genDto 入库参数
     * @return AjaxResult
     */
    public AjaxResult genData(GenDto genDto){
        String inBillCode = genDto.getInBillCode();
        String endAreaCode = genDto.getEndAreaCode();
        if(StrUtil.isBlank(inBillCode)){
            return  AjaxResult.error("请先验收！");
        }
        //根据入库单号查询入库单货物
        LambdaQueryWrapper<InbillGoods> goodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        goodsLambdaQueryWrapper.eq(InbillGoods::getInBillCode,inBillCode);
        goodsLambdaQueryWrapper.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        List<InbillGoods> inbillGoods = inbillGoodsMapper.selectList(goodsLambdaQueryWrapper);
        for (InbillGoods inbillGood : inbillGoods) {
            if(StrUtil.isBlank(inbillGood.getPartsCode())){
                inbillGood.setPartsCode(serialCodeUtils.getPartsCode("JKH"));
                inbillGood.setProduceTime(new Date());
            }
        }
        inbillGoodsService.updateBatchById(inbillGoods);

        //查询需要智能取盘的托盘数
        InbillDetail inbillDetail = new InbillDetail();
        inbillDetail.setInBillCode(inBillCode);
        List<InbillDetail> inbillDetails = inbillDetailService.aiTakeTrayList(inbillDetail);

        //循环 智能取盘
        for (InbillDetail detail : inbillDetails) {
            if(detail.getTrayCount().intValue() < 1){
               continue;
            }
            TrayDto map = new TrayDto();
            map.setNum(detail.getTrayCount().toString());
            map.setEndAreaCode(endAreaCode);
            map.setGoodsCode(detail.getGoodsCode());
            map.setInbillDetailId(detail.getId());
            //智能取盘
            trayService.aiTakeTray(map);
            //人工取盘
            // trayService.manMadeTakeTray(map);
            while (WmsTaskConstant.TAKE_TRAY_TASK.get()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // agv启用
        if (StrUtil.isBlank(endAreaCode)) {
            List<String> trayNo =new ArrayList<>();
            List<InbillGoods> inbillGood = new ArrayList<>();
            List<Tray> trays = new ArrayList<>();
            //查询理货区的托盘
            LambdaQueryWrapper<Location> locationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            locationLambdaQueryWrapper.select(Location::getTrayCode);
            locationLambdaQueryWrapper.eq(Location::getAreaId,"LHQ01");
            locationLambdaQueryWrapper.eq(Location::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            locationLambdaQueryWrapper.isNotNull(Location::getTrayCode);
            List<Location> locations = locationMapper.selectList(locationLambdaQueryWrapper);
            trayNo = locations.stream().map(Location::getTrayCode).collect(Collectors.toList());
            //组盘
            //获取托盘信息
            LambdaQueryWrapper<Tray> trayLambdaQueryWrapper = new LambdaQueryWrapper<>();
            trayLambdaQueryWrapper.in(Tray::getTrayCode,trayNo);
            trayLambdaQueryWrapper.eq(Tray::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            trays = trayMapper.selectList(trayLambdaQueryWrapper);

            //获取机件号信息
            List<String> detailId = inbillDetails.stream().map(InbillDetail::getId).collect(Collectors.toList());
            LambdaQueryWrapper<InbillGoods> goodsQueryWrapper = new LambdaQueryWrapper<>();
            goodsQueryWrapper.in(InbillGoods::getInbillDetailId,detailId);
            goodsQueryWrapper.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            inbillGood = inbillGoodsMapper.selectList(goodsQueryWrapper);
                while (trayNo.size()>0){
                    //组盘
                    smartDiscAssembly(trays,inbillDetails,inbillGood);
                    //上架
                    putaway(trayNo,endAreaCode);
                    try {
                        Thread.sleep(40000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //查询理货区的托盘
                    LambdaQueryWrapper<Location> locationLambdaQuery = new LambdaQueryWrapper<>();
                    locationLambdaQuery.select(Location::getTrayCode);
                    locationLambdaQuery.eq(Location::getAreaId,"LHQ01");
                    locationLambdaQuery.eq(Location::getDelFlag, DelFlagEnum.DEL_NO.getCode());
                    locationLambdaQuery.isNotNull(Location::getTrayCode);
                    List<Location> location = locationMapper.selectList(locationLambdaQuery);
                    trayNo = location.stream().map(Location::getTrayCode).collect(Collectors.toList());
                    //组盘
                    //获取托盘信息
                    LambdaQueryWrapper<Tray> trayLambdaQuery = new LambdaQueryWrapper<>();
                    trayLambdaQuery.in(Tray::getTrayCode,trayNo);
                    trayLambdaQuery.eq(Tray::getDelFlag, DelFlagEnum.DEL_NO.getCode());
                    trays = trayMapper.selectList(trayLambdaQuery);
                    //获取机件号信息
                    LambdaQueryWrapper<InbillGoods> goodsQuery = new LambdaQueryWrapper<>();
                    goodsQuery.in(InbillGoods::getInbillDetailId,detailId);
                    goodsQuery.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode());
                    goodsQuery.isNull(InbillGoods::getTrayCode);
                    inbillGood = inbillGoodsMapper.selectList(goodsQuery);
                }
        }else{
            List<AreaDto> areaLists = new ArrayList<>();
            areaLists = areaMapper.selectAllAreaByType(AreaTypeEnum.CCQ.getCode(), endAreaCode);
            if(areaLists.get(0).getEmptyNum()<1){
                return  AjaxResult.error("该库区没有空闲库位！");
            }
            //WCS任务信息对象
             LambdaQueryWrapper<WcsOperateTask> wcsOperateTaskQuery = Wrappers.lambdaQuery();
            wcsOperateTaskQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(WcsOperateTask::getTaskDeviceType, WcsTaskDeviceTypeEnum.STACKER.getCode())
                    .eq(WcsOperateTask::getOperateType, WmsWcsTypeEnum.TAKETRAY.getCode())
                    .eq(WcsOperateTask::getInBillNo, inBillCode);
            List<WcsOperateTask> wcsOperateTasks = wcsOperateTaskMapper.selectList(wcsOperateTaskQuery);
            // List<String> trayNo = wcsOperateTasks.stream().map(WcsOperateTask::getTrayNo).collect(Collectors.toList());
            List<String> waitTaskReqJson = wcsOperateTasks.stream().map(WcsOperateTask::getWaitTaskReqJson).collect(Collectors.toList());
            JSONArray jsonArray = new JSONArray(waitTaskReqJson);
            List<String> trayNo = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String startAreaCode = jsonObject.getStr("startAreaCode");
                if (startAreaCode.equals(endAreaCode)) {
                    String trayCode = jsonObject.getStr("trayCode");
                    trayNo.add(trayCode);
                }
            }
            redisCache.setCacheObject("flag","true",5, TimeUnit.MINUTES);
            while (trayNo.size()>areaLists.get(0).getEmptyNum()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                areaLists = areaMapper.selectAllAreaByType(AreaTypeEnum.CCQ.getCode(), endAreaCode);
                if(!"true".equals(redisCache.getCacheObject("flag"))){
                    return  AjaxResult.error("获取托盘超时！");
                }
            }
           //组盘
           discAssembly(trayNo,inbillDetails);
           //上架
           putaway(trayNo,endAreaCode);
        }
        return  AjaxResult.success();
    }


    //组盘
    private void discAssembly(List<String> trayNo, List<InbillDetail> inbillDetails){
        //组盘
        //获取托盘信息
        LambdaQueryWrapper<Tray> trayLambdaQueryWrapper = new LambdaQueryWrapper<>();
        trayLambdaQueryWrapper.in(Tray::getTrayCode,trayNo);
        trayLambdaQueryWrapper.eq(Tray::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        List<Tray> trays = trayMapper.selectList(trayLambdaQueryWrapper);

        //获取机件号信息
        List<String> detailId = inbillDetails.stream().map(InbillDetail::getId).collect(Collectors.toList());
        LambdaQueryWrapper<InbillGoods> goodsQueryWrapper = new LambdaQueryWrapper<>();
        goodsQueryWrapper.in(InbillGoods::getInbillDetailId,detailId);
        goodsQueryWrapper.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        goodsQueryWrapper.isNull(InbillGoods::getTrayCode);
        List<InbillGoods> inbillGood = inbillGoodsMapper.selectList(goodsQueryWrapper);

        Map<Integer, List<String>> onlyCodeMap = new HashMap<>();


        int flagKey  = 0;
        //根据货物组装数据
        for (InbillDetail detail : inbillDetails) {
            List<String> onlyCodes = inbillGood.stream().filter(e -> detail.getId().equals(e.getInbillDetailId()))
                    .map(InbillGoods::getOnlyCode).collect(Collectors.toList());
            int size = onlyCodes.size();
            int num = detail.getNum().intValue();
            int flagNum = 0;
            while (size>num){
                onlyCodeMap.put(flagKey, onlyCodes.subList(flagNum,num+flagNum));
                size = size - num;
                flagNum = flagNum + num;
                flagKey++;
            }
            if(size>0){
                onlyCodeMap.put(flagKey, onlyCodes.subList(flagNum,onlyCodes.size()));
                flagKey++;
            }
        }

        //封装组盘数据
        List<GroupDiskDto> groupDiskDtoList = new ArrayList<>();
        int flagMap = 0;
        for (Tray tray : trays) {
            GroupDiskDto groupDiskDto = new GroupDiskDto();
            groupDiskDto.setTrayCode(tray.getTrayCode());
            groupDiskDto.setOnlyCodeList(onlyCodeMap.get(flagMap));
            groupDiskDtoList.add(groupDiskDto);
            flagMap++;
        }
        //组盘
        for (GroupDiskDto groupDiskDto : groupDiskDtoList) {
            inbillDetailService.groupDisk(groupDiskDto);
        }
    }

    //上架
    private void putaway(List<String> trayNo,String endAreaCode){
        TrayDto trayDtoMap = new TrayDto();
        trayDtoMap.setStartAreaCode("LHQ01");
        trayDtoMap.setTrayCodeList(trayNo);
        trayDtoMap.setTaskType("0");
        trayDtoMap.setEndAreaCode(endAreaCode);
        trayService.putTray(trayDtoMap);
    }

    //智能组盘
    private void smartDiscAssembly(List<Tray> trays, List<InbillDetail> inbillDetails,List<InbillGoods> inbillGood){
        Map<Integer, List<String>> onlyCodeMap = new HashMap<>();
        int flagKey  = 0;
        //根据货物组装数据
        for (InbillDetail detail : inbillDetails) {
            List<String> onlyCodes = inbillGood.stream().filter(e -> detail.getId().equals(e.getInbillDetailId()))
                    .map(InbillGoods::getOnlyCode).collect(Collectors.toList());
            int size = onlyCodes.size();
            int num = detail.getNum().intValue();
            int flagNum = 0;
            while (size>num){
                onlyCodeMap.put(flagKey, onlyCodes.subList(flagNum,num+flagNum));
                size = size - num;
                flagNum = flagNum + num;
                flagKey++;
            }
            if(size>0){
                onlyCodeMap.put(flagKey, onlyCodes.subList(flagNum,onlyCodes.size()));
                flagKey++;
            }
        }

        //封装组盘数据
        List<GroupDiskDto> groupDiskDtoList = new ArrayList<>();
        int flagMap = 0;
        for (Tray tray : trays) {
            GroupDiskDto groupDiskDto = new GroupDiskDto();
            groupDiskDto.setTrayCode(tray.getTrayCode());
            groupDiskDto.setOnlyCodeList(onlyCodeMap.get(flagMap));
            groupDiskDtoList.add(groupDiskDto);
            flagMap++;
        }
        //组盘
        for (GroupDiskDto groupDiskDto : groupDiskDtoList) {
            inbillDetailService.groupDisk(groupDiskDto);
        }
    }
}
