package com.ruoyi.wms.wcstask.service;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.enums.WaitTaskEnum;
import com.ruoyi.wms.utils.constant.LhqLocationConstants;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.wcstask.vo.WaitTaskVo;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.wcstask.domain.Waittask;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.wcstask.mapper.WaittaskMapper;

import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 启用AGV时，wcs等待执行任务列Service接口
 *
 * @author ruoyi
 * @date 2023-02-24
 */
@Slf4j
@Service
public class WaittaskService extends ServiceImpl<WaittaskMapper, Waittask> {

    @Autowired
    private WaittaskMapper waittaskMapper;
    @Autowired
    private TasklogService tasklogService;
    @Autowired(required = false)
    private LocationMapper locationMapper;
    @Autowired(required = false)
    private TrayMapper trayMapper;
    @Autowired
    protected Validator validator;
    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;
    @Autowired(required = false)
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 查询启用AGV时，wcs等待执行任务列
     *
     * @param id 启用AGV时，wcs等待执行任务列主键
     * @return 启用AGV时，wcs等待执行任务列
     */
    public Waittask selectWaittaskById(String id){
        QueryWrapper<Waittask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return waittaskMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询启用AGV时，wcs等待执行任务列
     *
     * @param ids 启用AGV时，wcs等待执行任务列 IDs
     * @return 启用AGV时，wcs等待执行任务列
     */
    public List<Waittask> selectWaittaskByIds(String[] ids) {
        QueryWrapper<Waittask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return waittaskMapper.selectList(queryWrapper);
    }

    /**
     * 查询启用AGV时，wcs等待执行任务列列表
     *
     * @param waittask 启用AGV时，wcs等待执行任务列
     * @return 启用AGV时，wcs等待执行任务列集合
     */
    public List<Waittask> selectWaittaskList(Waittask waittask){
        QueryWrapper<Waittask> queryWrapper = getQueryWrapper(waittask);
        return waittaskMapper.select(queryWrapper);
    }

    /**
     * 新增启用AGV时，wcs等待执行任务列
     *
     * @param waittask 启用AGV时，wcs等待执行任务列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Waittask insertWaittask(Waittask waittask){
        waittask.setId(IdUtil.simpleUUID());
        waittask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        waittaskMapper.insert(waittask);
        return waittask;
    }

    /**
     * 批量保存等待任务
     * @param waittaskList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Waittask> waittaskList){
        List<Waittask> collect = waittaskList.stream().map(waittask -> {
            waittask.setId(IdUtil.simpleUUID());
            waittask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            waittask.setTaskStatus(WaitTaskEnum.NOT.getCode());
            return waittask;
        }).collect(Collectors.toList());
        return this.saveBatch(collect,collect.size());
    }

    /**
     * 修改启用AGV时，wcs等待执行任务列
     *
     * @param waittask 启用AGV时，wcs等待执行任务列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Waittask updateWaittask(Waittask waittask){
        waittaskMapper.updateById(waittask);
        return waittask;
    }

    /**
     * 批量删除启用AGV时，wcs等待执行任务列
     *
     * @param ids 需要删除的启用AGV时，wcs等待执行任务列主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWaittaskByIds(String[] ids){
        List<Waittask> waittasks = new ArrayList<>();
        for (String id : ids) {
            Waittask waittask = new Waittask();
            waittask.setId(id);
            waittask.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            waittasks.add(waittask);
        }
        return super.updateBatchById(waittasks) ? 1 : 0;
    }

    /**
     * 删除启用AGV时，wcs等待执行任务列信息
     *
     * @param id 启用AGV时，wcs等待执行任务列主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWaittaskById(String id){
        Waittask waittask = new Waittask();
        waittask.setId(id);
        waittask.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return waittaskMapper.updateById(waittask);
    }

    public QueryWrapper<Waittask> getQueryWrapper(Waittask waittask) {
        QueryWrapper<Waittask> queryWrapper = new QueryWrapper<>();
        if (waittask != null) {
            waittask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",waittask.getDelFlag());
            //状态（0-未执行；1-已执行）
            if (StrUtil.isNotEmpty(waittask.getTaskStatus())) {
                queryWrapper.eq("task_status",waittask.getTaskStatus());
            }
            //消费顺序
            if (waittask.getTaskOrder() != null) {
                queryWrapper.eq("task_order",waittask.getTaskOrder());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param waittaskList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<Waittask> waittaskList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(waittaskList) || waittaskList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Waittask waittask : waittaskList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                Waittask u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, waittask);
                    waittask.setId(IdUtil.simpleUUID());
                    waittask.setCreateBy(operName);
                    waittask.setCreateTime(new Date());
                    waittask.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    waittaskMapper.insert(waittask);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, waittask);
                    //todo 验证
                    //int count = waittaskMapper.checkCode(waittask);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        waittask.setId(u.getId());
                        waittask.setUpdateBy(operName);
                        waittask.setUpdateTime(new Date());
                        waittaskMapper.updateById(waittask);
                        successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第"+failureNum+"行数据导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
        }
        return successMsg.toString();
    }


    /**
     * 取盘分配理货区库位
     *
     * @param infoList trayCode 托盘, startLocationCode 起始库位, startAreaCode 起始库区, endAreaCode 结束库区
     * @return 取盘消息
     */
    @Transactional(rollbackFor = Exception.class)
    public String takeTray(List<WmsWcsInfo> infoList) {
        String msg = "已取托盘："+infoList.stream().map(info -> (String)info.get(WmsWcsInfo.TRAY_CODE)).collect(Collectors.toList());//取盘消息
        infoList = infoList.stream().map(info -> {
            info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
            return info;
        }).collect(Collectors.toList());
        //给wcs发送的数据
        String endAreaCode = (String) infoList.get(0).get(WmsWcsInfo.END_AREA_CODE);

        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位

            //start
//            //获取结束库区空闲库位
//            List<String> locationCodeList = locationMapper.getEmptyLocation(endAreaCode, infoList.size())
//                                                            .stream()
//                                                            .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
//            //end

            //CCQ01
            List<WmsWcsInfo> collect1 = infoList.parallelStream().filter(item -> "CCQ01".contains((CharSequence) item.get(WmsWcsInfo.START_AREA_CODE))).collect(Collectors.toList());
            List<EmptyLocationBo> emptyLocationlhq = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingOnes, endAreaCode, LhqLocationConstants.stagingOnes.length);
            emptyLocationlhq = emptyLocationlhq.subList(emptyLocationlhq.size() - collect1.size(), emptyLocationlhq.size());
            // 根据顺序进行逆序排序
            emptyLocationlhq = emptyLocationlhq.stream()
                    .sorted(Comparator.comparing(EmptyLocationBo::getOrderNum).reversed())
                    .collect(Collectors.toList());
            //获取CCQ01结束库区空闲库位
            List<String> locationCodeListOne = emptyLocationlhq
                    .stream()
                    .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            //CCQ02、CCQ03
            List<WmsWcsInfo> collect2 = infoList.parallelStream().filter(item -> !"CCQ01".contains((CharSequence) item.get(WmsWcsInfo.START_AREA_CODE))).collect(Collectors.toList());
            //获取CCQ01结束库区空闲库位
            List<String> locationCodeListTwo = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingTwos,endAreaCode, collect2.size())
                    .stream()
                    .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());

            List<String> locationCodeList = new ArrayList<>();
            locationCodeList.addAll(locationCodeListOne);
            locationCodeList.addAll(locationCodeListTwo);
            int size = collect1.size()+collect2.size();
            if (size > 0) {
                //锁定结束库区空闲库位
                LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                        .in(Location::getLocationCode, locationCodeList);
                locationMapper.update(null, locationUpdate);
                //被分配结束库位的托盘
//                List<WmsWcsInfo> infoYesList = infoList.subList(0, size);
                for (int i = 0; i < collect1.size(); i++) {
                    Object chidList = collect1.get(i).get(WmsWcsInfo.CHILD_INFO_LIST);
                    if (chidList != null) {
                        List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                        for (WmsWcsInfo chid : chids) {
                            if (StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE) + "")
                                    && chid.get(WmsWcsInfo.TYPE).equals(WmsWcsTypeEnum.TAKETRAY.getCode())) {
                                chid.put(WmsWcsInfo.END_LOCATION_CODE, locationCodeListOne.get(i));
                            }
                        }
                    }
                    collect1.get(i).put(WmsWcsInfo.END_LOCATION_CODE, locationCodeListOne.get(i));
                }
                for (int i = 0; i < collect2.size(); i++) {
                    Object chidList = collect2.get(i).get(WmsWcsInfo.CHILD_INFO_LIST);
                    if (chidList != null) {
                        List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                        for (WmsWcsInfo chid : chids) {
                            if (StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE) + "")
                                    && chid.get(WmsWcsInfo.TYPE).equals(WmsWcsTypeEnum.TAKETRAY.getCode())) {
                                chid.put(WmsWcsInfo.END_LOCATION_CODE, locationCodeListTwo.get(i));
                            }
                        }
                    }
                    collect2.get(i).put(WmsWcsInfo.END_LOCATION_CODE, locationCodeListTwo.get(i));
                }
            }
            List<WmsWcsInfo> infoYesList = new ArrayList<>();
            int maxSize = Math.max(collect2.size(), collect1.size());
            for (int i = 0; i < maxSize; i++) {
                if (i < collect2.size()) {
                    infoYesList.add(collect2.get(i));
                }
                if (i < collect1.size()) {
                    infoYesList.add(collect1.get(i));
                }
            }

            //start
            //结束库位的数量
//            int size = locationCodeList.size();
//            if (size > 0) {
//                //锁定结束库区空闲库位
//                LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
//                locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
//                        .in(Location::getLocationCode, locationCodeList);
//                locationMapper.update(null, locationUpdate);
//                //被分配结束库位的托盘
//                List<WmsWcsInfo> infoYesList = infoList.subList(0, size);
//                //分配结束库位
//                for (int i = 0; i < infoYesList.size(); i++) {
//                    Object chidList = infoYesList.get(i).get(WmsWcsInfo.CHILD_INFO_LIST);
//                    if(chidList != null){
//                        List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
//                        for (WmsWcsInfo chid : chids) {
//                            if(StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE)+"")
//                                    && chid.get(WmsWcsInfo.TYPE).equals(WmsWcsTypeEnum.TAKETRAY.getCode())){
//                                chid.put(WmsWcsInfo.END_LOCATION_CODE, locationCodeList.get(i));
//                            }
//                        }
//                    }
//                    infoYesList.get(i).put(WmsWcsInfo.END_LOCATION_CODE, locationCodeList.get(i));
//                }
                //end

                //将分配了结束库位的托盘数据给wcs
                //记录给wcs任务的数据
                tasklogService.saveBatch(infoYesList);
                //把任务的数据给wcs
                List<WmsToWcsTaskReq> collect = infoYesList.stream().map(info -> {
                    String jsonStr = JSONObject.toJSONString(info);
                    return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
                }).collect(Collectors.toList());
                new Thread(() -> {
                    wcsTaskApiService.agvLinkAgeStacker(collect);
                }).start();
            }
            //if (infoList.size() > size) {
            //    //没被分配结束库位的托盘
            //    List<WmsWcsInfo> infoNoList = infoList.subList(size, infoList.size());
            //    //存储入数据库中
            //    List<Waittask> waittaskList = new ArrayList<>();
            //    for (int i = 0; i < infoNoList.size(); i++) {
            //        WmsWcsInfo info = infoNoList.get(i);
            //        Waittask waittask = new Waittask();
            //        waittask.setTaskData(JSON.toJSONString(info));
            //        waittask.setTaskOrder((long) i);
            //        waittaskList.add(waittask);
            //    }
            //    saveBatch(waittaskList);
            //}
            ////查询等待任务数量
            //LambdaQueryWrapper<Waittask> waittaskQueryWrapper = Wrappers.lambdaQuery();
            //waittaskQueryWrapper.eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
            //        .eq(Waittask::getTaskStatus,WaitTaskEnum.NOT.getCode());
            //Long aLong = waittaskMapper.selectCount(waittaskQueryWrapper);
            //if (aLong>0){
            //    msg += "；共有"+aLong+"个托盘处于取盘任务队列中";
            //}
         else {//agv不启用，直接将托盘数据给wcs
            for (WmsWcsInfo info:infoList) {
                info.put(WmsWcsInfo.END_LOCATION_CODE,"csd");
                Object chidList = info.get(WmsWcsInfo.CHILD_INFO_LIST);
                if(chidList != null){
                    List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                    for (WmsWcsInfo chid : chids) {
                        if(StrUtil.isEmptyOrUndefined(chid.get(WmsWcsInfo.END_LOCATION_CODE)+"")){
                           chid.put(WmsWcsInfo.END_LOCATION_CODE,"csd");
                        }
                    }
                }
            }
            //记录给wcs任务的数据
            tasklogService.saveBatch(infoList);
            //把任务的数据给wcs
            List<WmsToWcsTaskReq> collect = infoList.stream().map(info -> {
                String jsonStr = JSONObject.toJSONString(info);
                return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
            }).collect(Collectors.toList());
            new Thread(() -> {
                wcsTaskApiService.agvLinkAgeStacker(collect);
            }).start();
        }
        return msg;
    }


    /**
     * 消费在等待的任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void consumeTask(){
        LambdaQueryWrapper<Waittask> waittaskQuery = Wrappers.lambdaQuery();
        waittaskQuery.eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(Waittask::getTaskStatus,WaitTaskEnum.NOT.getCode())
                .orderByAsc(BaseEntity::getCreateTime)
                .orderByAsc(Waittask::getTaskOrder)
                .last(" limit 1");
        Waittask waittask = waittaskMapper.selectOne(waittaskQuery);
        if (waittask!=null){
            WmsWcsInfo wmsWcsInfo = JSON.parseObject(waittask.getTaskData(), WmsWcsInfo.class);
            //结束库区编码
            String endAreaCode = (String)wmsWcsInfo.get(WmsWcsInfo.END_AREA_CODE);
            //查询一个结束库区空闲库位
            List<String> locationCodeList = locationMapper.getEmptyLocation(endAreaCode, 1)
                                                            .stream()
                                                            .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            if (locationCodeList.size()>0){
                //锁定结束库区空闲库位
                LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                        .in(Location::getLocationCode, locationCodeList);
                locationMapper.update(null, locationUpdate);
                //修改任务为已执行
                LambdaUpdateWrapper<Waittask> waittaskUpdate = Wrappers.lambdaUpdate();
                waittaskUpdate.set(Waittask::getTaskStatus,WaitTaskEnum.ALREADY.getCode())
                        .eq(Waittask::getId,waittask.getId());
                waittaskMapper.update(null,waittaskUpdate);
                //分配end库位编码
                wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE,locationCodeList.get(0));
                //记录给wcs任务的数据
                tasklogService.insertTasklog(wmsWcsInfo);
                //把任务的数据给wcs
                String jsonStr = JSONObject.toJSONString(wmsWcsInfo);
                List<WmsToWcsTaskReq> collect = new ArrayList<>();
                collect.add(JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class));
//                new Thread(() -> {
                    wcsTaskApiService.agvLinkAgeStacker(collect);
//                }).start();
            }
        }
    }


    /**
     * 查看等待取盘任务列表
     * @return 结果
     */
    public List<WaitTaskVo> selectWaitTask() {
        LambdaQueryWrapper<Waittask> waittaskQuery = Wrappers.lambdaQuery();
        waittaskQuery.eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .eq(Waittask::getTaskStatus,WaitTaskEnum.NOT.getCode())
                .orderByAsc(BaseEntity::getCreateTime)
                .orderByAsc(Waittask::getTaskOrder);
        List<Waittask> waittaskList = waittaskMapper.selectList(waittaskQuery);
        List<WaitTaskVo> collect = waittaskList.stream().map(t -> {
            WmsWcsInfo wmsWcsInfo = JSON.parseObject(t.getTaskData(), WmsWcsInfo.class);
            WaitTaskVo waitTaskVo = new WaitTaskVo();
            waitTaskVo.setTrayCode((String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE));
            return waitTaskVo;
        }).collect(Collectors.toList());
        return collect;
    }


}
