package com.ruoyi.wms.basics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.zxing.WriterException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.BusinessConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CodeGeneratorUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.domain.TrayModel;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.dto.PrintDataDto;
import com.ruoyi.wms.basics.dto.TrayDto;
import com.ruoyi.wms.basics.mapper.*;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.enums.ListingEnum;
import com.ruoyi.wms.enums.LocationTypeEnum;
import com.ruoyi.wms.enums.TacticsEnum;
import com.ruoyi.wms.global.WmsTaskConstant;
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.ruoyi.wms.nolist.mapper.NolistWaitMapper;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.mapper.OutbillGoodsMapper;
import com.ruoyi.wms.utils.constant.LhqLocationConstants;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.mapper.ListingDetailMapper;
import com.ruoyi.wms.warehousing.service.InbillDetailService;
import com.ruoyi.wms.wcstask.service.TasklogService;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * 托盘基本信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-02
 */
@Slf4j
@Service
public class TrayService extends ServiceImpl<TrayMapper, Tray> {

    @Autowired
    protected Validator validator;

    @Autowired(required = false)
    private TrayMapper trayMapper;

    @Autowired(required = false)
    private TrayService trayService;

    @Autowired(required = false)
    private TrayModelMapper trayModelMapper;

    @Autowired(required = false)
    private AreaMapper areaMapper;

    @Autowired(required = false)
    private ListingDetailMapper listingDetailMapper;

    @Autowired(required = false)
    private NolistWaitMapper nolistWaitMapper;

    @Autowired
    private LocationService locationService;

    @Autowired(required = false)
    private WmsTacticsConfigMapper wmsTacticsConfigMapper;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired(required = false)
    private OutbillGoodsMapper outbillGoodsMapper;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    private WmsWarehouseCheckDetailService checkDetailService;

    @Autowired
    private InbillDetailService inbillDetailService;

    @Autowired
    private WaittaskService waittaskService;

    @Autowired
    private TasklogService tasklogService;

    @Autowired(required = false)
    private WarehouseMapper warehouseMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    /**
     * 查询托盘基本信息
     *
     * @param id 托盘基本信息主键
     * @return 托盘基本信息
     */
    public Tray selectTrayById(String id) {
        QueryWrapper<Tray> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return trayMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询托盘基本信息
     *
     * @param ids 托盘基本信息 IDs
     * @return 托盘基本信息
     */
    public List<Tray> selectTrayByIds(String[] ids) {
        QueryWrapper<Tray> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return trayMapper.selectList(queryWrapper);
    }

    /**
     * 查询托盘基本信息列表
     *
     * @param tray 托盘基本信息
     * @return 托盘基本信息集合
     */
    public List<Tray> selectTrayList(Tray tray) {
        QueryWrapper<Tray> queryWrapper = getQueryWrapper(tray);
        return trayMapper.select(queryWrapper);
    }

    /**
     * 新增托盘基本信息
     *
     * @param tray 托盘基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tray insertTray(Tray tray) {
        tray.setId(IdUtil.simpleUUID());
        tray.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        tray.setEnableStatus(EnableStatus.ENABLE.getCode());
        trayMapper.insert(tray);
        return tray;
    }

    /**
     * 修改托盘基本信息
     *
     * @param tray 托盘基本信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Tray updateTray(Tray tray) {
        if (StrUtil.isNotEmpty(tray.getId())) {
            Tray item = trayMapper.selectById(tray.getId());
            if (ObjectUtil.isNotNull(item)) {
                if (StrUtil.isNotEmpty(item.getGoodsCode()) && EnableStatus.DISABLE.getCode().equals(tray.getEnableStatus())) {
                    throw new ServiceException("当前托盘上有货物，无法禁用");
                }
            }
            trayMapper.updateById(tray);
            return tray;
        } else {
            throw new ServiceException("托盘编号缺失");
        }
    }

    /**
     * 批量删除托盘基本信息
     *
     * @param ids 需要删除的托盘基本信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrayByIds(String[] ids) {
        List<Tray> trays = new ArrayList<>();
        for (String id : ids) {
            Tray tray = new Tray();
            tray.setId(id);
            tray.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            trays.add(tray);
        }
        return super.updateBatchById(trays) ? 1 : 0;
    }

    /**
     * 删除托盘基本信息信息
     *
     * @param id 托盘基本信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteTrayById(String id) {
        Tray tray = new Tray();
        tray.setId(id);
        tray.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return trayMapper.updateById(tray);
    }

    public QueryWrapper<Tray> getQueryWrapper(Tray tray) {
        QueryWrapper<Tray> queryWrapper = new QueryWrapper<>();
        if (tray != null) {
            tray.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", tray.getDelFlag());
            //托盘编码
            if (StrUtil.isNotEmpty(tray.getTrayCode())) {
                queryWrapper.like("tray_code", tray.getTrayCode());
            }
            //托盘名称
            if (StrUtil.isNotEmpty(tray.getTrayName())) {
                queryWrapper.like("tray_name", tray.getTrayName());
            }
            //托盘简称
            if (StrUtil.isNotEmpty(tray.getTraySimpleName())) {
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("tray_simple_name", tray.getTraySimpleName()).or();
                    QueryWrapper.like("tray_code", tray.getTraySimpleName()).or();
                    QueryWrapper.like("tray_name", tray.getTraySimpleName());
                });
            }
            //所属仓库
            if (StrUtil.isNotEmpty(tray.getWarehouseId())) {
                queryWrapper.eq("warehouse_id", tray.getWarehouseId());
            }
            //所属库区
            if (StrUtil.isNotEmpty(tray.getAreaId())) {
                queryWrapper.eq("area_id", tray.getAreaId());
            }
            //所属库位编号
            if (StrUtil.isNotEmpty(tray.getLocationId())) {
                queryWrapper.eq("location_id", tray.getLocationId());
            }
            //长(m)
            if (tray.getTrayLength() != null) {
                queryWrapper.eq("tray_length", tray.getTrayLength());
            }
            //宽(m)
            if (tray.getTrayWidth() != null) {
                queryWrapper.eq("tray_width", tray.getTrayWidth());
            }
            //高(m)
            if (tray.getTrayHeight() != null) {
                queryWrapper.eq("tray_height", tray.getTrayHeight());
            }
            //体积(m³)
            if (tray.getTrayVolume() != null) {
                queryWrapper.eq("tray_volume", tray.getTrayVolume());
            }
            //可用容量(m³)
            if (tray.getTrayUsableVolume() != null) {
                queryWrapper.eq("tray_usable_volume", tray.getTrayUsableVolume());
            }
            //限重(kg)
            if (tray.getTrayLimitWeight() != null) {
                queryWrapper.eq("tray_limit_weight", tray.getTrayLimitWeight());
            }
            //可用重量(kg)
            if (tray.getTrayUsableWeight() != null) {
                queryWrapper.eq("tray_usable_weight", tray.getTrayUsableWeight());
            }
            //启用状态(0:启用  1:禁用)
            if (StrUtil.isNotEmpty(tray.getEnableStatus())) {
                queryWrapper.eq("enable_status", tray.getEnableStatus());
            }
            //是否为空盘（0-空的 1-不空）
            if (StrUtil.isNotEmpty(tray.getEmptyStatus())) {
                queryWrapper.eq("empty_status", tray.getEmptyStatus());
            }
            //根据ids查询数据
            if (CollectionUtil.isNotEmpty(tray.getIds())) {
                queryWrapper.in("id", tray.getIds());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param trayList      模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Tray> trayList, boolean updateSupport, String operName, String modelCode, String warehouse, String area, String location) {
        if (StringUtils.isNull(trayList) || trayList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int i = 1;
        List<String> trayCode = new ArrayList<>();
        for (Tray tray : trayList) {
            if (trayCode.contains(tray.getTrayCode())) {
                throw new ServiceException("导入数据中的第" + (i + 1) + "行的托盘编码重复");
            } else {
                trayCode.add(tray.getTrayCode());
            }
            i++;
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        QueryWrapper<TrayModel> trayQueryWrapper = new QueryWrapper<>();
        trayQueryWrapper.eq("tray_Model_Code", modelCode).eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        TrayModel model = trayModelMapper.selectOne(trayQueryWrapper);
        List<Tray> trays = new ArrayList<>();
        for (Tray tray : trayList) {
            if (null == tray) {
                throw new ServiceException("导入数据模板不正确，请重新选择");
            }
            if (StringUtils.isEmpty(tray.getTrayCode())) {
                throw new ServiceException("导入托盘编码不能为空！");
            } else if (tray.getTrayCode().length() > 20) {
                throw new ServiceException("导入托盘编码长度不能超20位！");
            }
            try {
                tray.setTrayModelCode(modelCode);
                tray.setTrayLength(model.getModelLength());
                tray.setTrayWidth(model.getModelWidth());
                tray.setTrayHeight(model.getModelHeight());
                tray.setTrayVolume(model.getModelVolume());
                tray.setTrayUsableVolume(model.getModelVolume());
                tray.setTrayLimitWeight(model.getModelWeight());
                tray.setTrayUsableWeight(model.getModelWeight());
                Tray u = trayMapper.selectDataByCode(tray.getTrayCode());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, tray);
                    tray.setId(IdUtil.simpleUUID());
                    tray.setCreateBy(operName);
                    tray.setCreateTime(new Date());
                    tray.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    tray.setEnableStatus("1");
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、托盘 " + tray.getTrayCode() + " 导入成功");
                    trays.add(tray);
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, tray);
                    tray.setId(u.getId());
                    tray.setEnableStatus(u.getEnableStatus());
                    int count = trayMapper.checkCode(tray);
                    if (count > 0) {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、托盘 " + tray.getTrayCode() + "的编码 已存在");
                    } else {
                        tray.setUpdateBy(operName);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、托盘 " + tray.getTrayCode() + " 更新成功");
                        trays.add(tray);
                    }
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、托盘 " + tray.getTrayCode() + "的编码 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
                failureMsg.append(msg + "数据类型不匹配或者长度太长");
                log.error(msg, e);
            }
        }
        if (trays.size() > 0) {
            trayService.saveOrUpdateBatch(trays);
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    public AjaxResult checkData(Tray tray) {
        //验证编码唯一性
        int count = trayMapper.checkCode(tray);
        if (count > 0) {
            throw new ServiceException("该托盘编码已存在，请保证托盘编码唯一");
        }
        return AjaxResult.success(true);
    }


    /**
     * 根据托盘编码查询托盘信息
     *
     * @param trayCode 托盘编码
     * @return 托盘信息
     */
    public Tray selectByTrayCode(String trayCode) {
        QueryWrapper<Tray> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tray_code", trayCode);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.last("limit 1");
        return trayMapper.selectOne(queryWrapper);
    }

    /**
     * 根据托盘编码查询托盘信息
     *
     * @param trayCodeList 托盘编码list
     * @return 托盘信息
     */
    public List<Tray> selectByTrayCodeList(List<String> trayCodeList) {
        QueryWrapper<Tray> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("tray_code", trayCodeList);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return trayMapper.select(queryWrapper);
    }


    /**
     * 根据不同条件生成二维码数据
     *
     * @param tray
     * @return
     */
    public List getPrintData(Tray tray) throws IOException, WriterException {
        List<Tray> trayList = new ArrayList<>();
        QueryWrapper<Tray> queryWrapper = null;
        //选择数据查询
        if (StringUtils.isNotNull(tray.getIds())) {
            Tray t = new Tray();
            t.setIds(tray.getIds());
            queryWrapper = getQueryWrapper(t);
        } else {//过滤条件查询
            queryWrapper = getQueryWrapper(tray);
        }
        queryWrapper.orderByDesc("CONVERT(tray_code,SIGNED)");
        trayList = trayMapper.selectList(queryWrapper);
        //组装数据
        List<PrintDataDto> result = new ArrayList<>();
        PrintDataDto map = null;
        for (Tray d : trayList) {
            map = new PrintDataDto();
            map.setCode(d.getTrayCode());
            map.setName(d.getTrayName());
            map.setLength(d.getTrayLength().intValue() + "");
            map.setWidth(d.getTrayWidth().intValue() + "");
            map.setHeight(d.getTrayHeight().intValue() + "");
            map.setWeight(d.getTrayLimitWeight().intValue() + "");
            String text = "code=" + d.getTrayCode() + ";" + "name=" + d.getTrayName() + ";type=tray;";
            String filePath = "/qrcode/" + IdUtil.randomUUID() + ".png";
            String basePath = RuoYiConfig.getProfile();
            File baseFile = new File(basePath);
            if(!baseFile.exists()){
                baseFile.mkdirs();
            }
            CodeGeneratorUtil.generateQRCodeImage(text, 100, 100, RuoYiConfig.getProfile() + filePath);
            map.setUrl("/profile" + filePath);
            result.add(map);
        }
        return result;
    }


    /**
     * 人工取盘
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult manMadeTakeTray(TrayDto map) {
        //结束库区编码
        String endAreaCode = map.getEndAreaCode();
        //取托盘数
        Integer num = Integer.valueOf(map.getNum());
        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
        }
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
            if ((Objects.equals(map.getDeviceNo(), "141") || Objects.equals(map.getPointAreaCode(), "CCQ01"))) {
                boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK_141.get();
                if (noTask) {
                    throw new ServiceException("141号AGV取盘操作正在进行中，请稍后再试");
                }
                retrievalEnableAGV(map,num,endAreaCode,areaIds,WmsTaskConstant.TAKE_TRAY_TASK_141::compareAndSet);
            }else{
                boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK_140.get();
                if (noTask) {
                    throw new ServiceException("140号AGV取盘操作正在进行中，请稍后再试");
                }
                retrievalEnableAGV(map,num,endAreaCode,areaIds,WmsTaskConstant.TAKE_TRAY_TASK_140::compareAndSet);
            }
        }else{
            retrievalStacker(map,num,endAreaCode,areaIds);
        }
        return AjaxResult.success("人工取盘任务下发成功");
    }

    //agv取盘
    public void retrievalEnableAGV(TrayDto map,Integer num, String endAreaCode,List<String> areaIds,BiPredicate<Boolean, Boolean> compareAndSet){
        //设备id
        String deviceNo = map.getDeviceNo();
        //指定库区
        String pointAreaCode = map.getPointAreaCode();
        //检查数据和策略
        List<AreaDto> areaDtos = checTask(map,num,areaIds);
        if ((Objects.equals(deviceNo, "141") || Objects.equals(pointAreaCode, "CCQ01"))) {
            areaDtos = areaDtos.parallelStream().filter(item -> "CCQ01".contains( item.getAreaCode())).collect(Collectors.toList());
            if (!areaIds.contains("CCQ01")) {
                throw new ServiceException("CCQ01" + "库区的堆垛机目前都处于手动模式，取盘失败");
            }
            //所有库区的空托盘总数
            long emptyNum = areaDtos.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).sum();
            if (emptyNum < num) {
                throw new ServiceException("1号存储区托盘数量不足，剩余数量：" + emptyNum);
            }
            List<String> locationCodeListOne = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingOnes,endAreaCode, num.intValue())
                    .stream()
                    .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            if(locationCodeListOne.size() < num.intValue()){
                throw new ServiceException("理货区库位不足,141号AGV理货区目前空余库位数量为:"+ locationCodeListOne.size());
            }
            LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getStartAreaCode,"CCQ01");
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskStatus, "0");
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
            if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
                throw new ServiceException("141号AGV存在未执行的取盘任务，请稍后再试");
            }
        }else{
            areaDtos = areaDtos.parallelStream().filter(item -> !"CCQ01".contains( item.getAreaCode())).collect(Collectors.toList());
            if (!areaIds.contains("CCQ02") && !areaIds.contains("CCQ03") ) {
                throw new ServiceException("CCQ02和CCQ03" + "库区的堆垛机目前都处于手动模式，取盘失败");
            }
            //所有库区的空托盘总数
            long emptyNum = areaDtos.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).sum();
            if (emptyNum < num) {
                throw new ServiceException("2号存储区、3号存储区托盘数量不足，剩余数量：" + emptyNum);
            }
            List<String> locationCodeListTwo = locationMapper.getEmptyLocationlhq(LhqLocationConstants.stagingTwos,endAreaCode, num.intValue())
                    .stream()
                    .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            if(locationCodeListTwo.size() < num.intValue()){
                throw new ServiceException("理货区库位不足,140号AGV理货区目前空余库位数量为:"+ locationCodeListTwo.size());
            }
            List<String> ccqList = new ArrayList<>();
            ccqList.add("CCQ02");
            ccqList.add("CCQ03");
            LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
            operateTaskLambdaQueryWrapper.in(WcsOperateTask::getStartAreaCode,ccqList);
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskStatus, "0");
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
            if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
                throw new ServiceException("140号AGV存在未执行的取盘任务，请稍后再试");
            }
        }
        //组装并下发任务
        startDeviceTread(areaDtos,num,endAreaCode,compareAndSet);

    }

    //堆垛机取盘
    public void retrievalStacker(TrayDto map,Integer num, String endAreaCode,List<String> areaIds ){
        boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK.get();
        if (noTask) {
            throw new ServiceException("取盘操作正在进行中，请稍后再试");
        }
        //检查数据和策略
        List<AreaDto> areaDtos = checTask(map,num,areaIds);
        //所有库区的空托盘总数
        long emptyNum = areaDtos.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).sum();
        if (emptyNum < num) {
            throw new ServiceException("托盘数量不足，剩余数量：" + emptyNum);
        }
        //组装并下发任务
        startDeviceTread(areaDtos,num,endAreaCode,WmsTaskConstant.TAKE_TRAY_TASK::compareAndSet);
    }

    //检查人工取盘数据
    private List<AreaDto> checTask(TrayDto map,Integer num,List<String> areaIds ){
        if (checkDetailService.haveChecking()) {
            throw new ServiceException("盘点任务进行中，取盘失败");
        }
        if (StringUtils.isNull(map)) {
            throw new ServiceException("人工取盘的参数不能为空");
        }
        if (num == 0) {
            throw new ServiceException("取盘数量不可为零");
        }
        if (num < 0) {
            throw new ServiceException("取盘数量不可小于零");
        }
        //库区空盘数列表 areaCode 库区编码, emptyCount 库区内空托盘数量
        List<AreaDto> dataAreaMapList = trayMapper.getEmptyTrayCountByAreaType(AreaTypeEnum.CCQ.getCode());
        List<AreaDto> areaDtos = dataAreaMapList.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());

        if(map.getGetTrayType().equals("avgGet")){
//            LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
//            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskStatus, "0");
//            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
//            if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
//                throw new ServiceException("库区的堆垛机取盘操作有未执行的任务，请稍后再试");
//            }
        }else if(map.getGetTrayType().equals("pointGet")){
            dataAreaMapList = dataAreaMapList.stream().filter(item->item.getAreaCode().equals(map.getPointAreaCode())).collect(Collectors.toList());
            if(!areaIds.contains(map.getPointAreaCode())){
                throw new ServiceException(map.getPointAreaCode()+"库区的堆垛机目前处于手动模式，取盘失败");
            }
            LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getStartAreaCode, map.getPointAreaCode());
            operateTaskLambdaQueryWrapper.in(WcsOperateTask::getTaskStatus, "0",'1');
            operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
            if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
                throw new ServiceException(map.getPointAreaCode()+"库区的堆垛机取盘操作有未执行完成的任务，请稍后再试");
            }
            areaDtos = dataAreaMapList.parallelStream().filter(item -> item.getAreaCode().equals(map.getPointAreaCode())).collect(Collectors.toList());
        }
        return  areaDtos;
    }

    //组装下发任务
    public void startDeviceTread(List<AreaDto> finalAreaDtos, Integer num, String endAreaCode, BiPredicate<Boolean, Boolean> task){
        try {
            new Thread(() -> {
                task.test(false, true);
                List<String> hasParentLocationCodes = new ArrayList<>();
                //从库区平均获取空托盘
                int size = finalAreaDtos.size();//库区总数
                List<AreaDto> areaMapList = finalAreaDtos;
                if (size == 1) {
                    areaMapList.get(0).setSureCount(num.longValue());
                } else {
                    // 根据空托盘库位从大到小排序
                    areaMapList = areaMapList.stream()
                            .sorted(Comparator.comparing(AreaDto::getEmptyNum).reversed()) // 根据空闲数量进行逆序排序
                            .collect(Collectors.toList());
                    for (int i = 0; i < num; i++) {
                        AreaDto areaMap = areaMapList.get(i % size);//当前该取的库区
                        long emptyCount = areaMap.getEmptyNum();//库区内的空托盘数量
                        if (emptyCount > 0) {
                            //从该库区取一个空托盘
                            areaMap.setEmptyNum(emptyCount - 1L);
                            areaMap.setSureCount(areaMap.getSureCount() + 1L);
                        } else {
                            //从库区列表中找到空托盘最大的库区，并从该库区取一个托盘
                            long emptyCountMax = areaMapList.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).max().getAsLong();
                            for (int j = 0; j < areaMapList.size(); j++) {
                                AreaDto areaMap3 = areaMapList.get(j);
                                if (areaMap3.getEmptyNum() == emptyCountMax) {
                                    areaMap3.setEmptyNum(areaMap3.getEmptyNum() - 1L);
                                    if (areaMap3.getSureCount() == 0) {
                                        areaMap3.setSureCount(1L);
                                    } else {
                                        areaMap3.setSureCount(areaMap3.getSureCount() + 1);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                //组装数据，取盘
                List<WmsWcsInfo> infoList = new ArrayList<>();
                Map<Integer, WmsWcsInfo> infoMap = new HashMap<>();
                areaMapList = areaMapList.stream().sorted(Comparator.comparing(areaMap -> areaMap.getAreaCode())).collect(Collectors.toList());
                for (int i = 0; i < areaMapList.size(); i++) {
                    AreaDto areaMap = areaMapList.get(i);
                    if (areaMap != null && areaMap.getSureCount() == 0) {
                        continue;
                    }
                    //trayCode 空托盘, locationCode 空托盘所在的库位
                    List<LocationMapVo> emptyTrayList = trayMapper.getEmptyTray(areaMap.getAreaCode(), Integer.valueOf(areaMap.getSureCount() + ""));

                    //锁定托盘所在库位
                    List<String> locationCodeList = emptyTrayList.stream().map(emptyTray -> emptyTray.getLocationCode()).collect(Collectors.toList());
                    LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                    locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                            .in(Location::getLocationCode, locationCodeList);
                    locationService.getBaseMapper().update(null, locationUpdate);
                    //交叠组装数据
                    for (int j = 0; j < emptyTrayList.size(); j++) {
                        LocationMapVo emptyTray = emptyTrayList.get(j);
                        if(emptyTray.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())){
                            WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                            info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                            info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                            info.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                            info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                            infoMap.put(i + size * j, info);
                            hasParentLocationCodes.add(emptyTray.getLocationCode());
                        }else{
                            Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                                    .eq("enable_status", EnableStatus.ENABLE.getCode())
                                    .eq("location_code", emptyTray.getLocationCode())
                            );
                            Location parentLocation = locationService.getParentLocation(childLocation, areaMap.getAreaCode());

                            //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                            if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                                WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                                info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                                info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                                info.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                                infoMap.put(i + size * j, info);
                            }else{
                                String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(areaMap.getAreaCode());

                                List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                                WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                                info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);

                                //1.母库位移动至移库库位
                                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                                childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                                childInfoList.add(childInfo);

                                //2.取盘具体任务
                                WmsWcsInfo moveInfo = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                                moveInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                                moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                                moveInfo.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                                moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                                childInfoList.add(moveInfo);


                                //移库库位回至母库位
                                WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                                childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                                childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                                childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                                childInfoList.add(childInfo3);

                                info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                                infoMap.put(i + size * j, info);
                            }
                        }

                    }
                }
                Set<Integer> integers = infoMap.keySet();
                List<Integer> collect = integers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                for (Integer integer : collect) {
                    infoList.add(infoMap.get(integer));
                }
                //取盘
                String msg = waittaskService.takeTray(infoList);
                task.test(true, false);
            }).start();
        } catch (Exception e) {
            task.test(true, false);
        }
    }

    /**
     * 智能取盘
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult aiTakeTray(TrayDto map) {
        boolean noTask = WmsTaskConstant.TAKE_TRAY_TASK.get();
        if (noTask) {
            throw new ServiceException("取盘操作正在进行中，请稍后再试");
        }
        if (checkDetailService.haveChecking()) {
            throw new ServiceException("盘点任务进行中，取盘失败");
        }
        if (StringUtils.isNull(map)) {
            throw new ServiceException("智能取盘的参数不能为空");
        }
        //结束库区编码
        String endAreaCode = map.getEndAreaCode();

        //货物编码
        String goodsCode = map.getGoodsCode();
        //入库单详情主键
        String inbillDetailId = map.getInbillDetailId();
        InbillDetail detail = inbillDetailService.getById(inbillDetailId);
        String inbillCode = detail.getInBillCode();
        if (StringUtils.isEmpty(endAreaCode) || StringUtils.isEmpty(goodsCode) || StringUtils.isEmpty(inbillDetailId)) {
            throw new ServiceException("参数不全");
        }
        //取托盘数
        Long num = Long.valueOf(map.getNum());
        if (num == 0) {
            throw new ServiceException("取盘数量不可为零");
        }

        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
            //获取理货区空闲库位
            List<String> locationCodeList = locationService.getBaseMapper().getEmptyLocation(endAreaCode, num.intValue())
                    .stream()
                    .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            if(locationCodeList.size() < num.intValue()){
                throw new ServiceException("理货区库位不足,理货区目前空余库位数量为:"+locationCodeList.size());
            }
        }

        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
        }

        //库区空盘数列表 areaCode 库区编码, emptyCount 库区内空托盘数量
        List<AreaDto> dataAreaMapList = trayMapper.getEmptyTrayCountByAreaType(AreaTypeEnum.CCQ.getCode());
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        List<AreaDto> areaDtos = dataAreaMapList.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());


        //所有库区的空托盘总数
        Long emptyNum = areaDtos.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).sum();
        if (emptyNum < num) {
            throw new ServiceException("托盘数量不足，剩余数量：" + emptyNum);
        }
        try {
            new Thread(() -> {
                WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(false, true);
                List<String> hasParentLocationCodes = new ArrayList<>();
                List<AreaDto> areaMapList = areaDtos;
                int size = areaMapList.size();//库区总数
                if (size == 1) {
                    areaMapList.get(0).setSureCount(num.longValue());
                } else {
                    //获取全局策略
                    //LambdaQueryWrapper<WmsTacticsConfig> tacticsQuery = Wrappers.lambdaQuery();
                    //tacticsQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    //        .last(" limit 1");
                    //WmsTacticsConfig wmsTacticsConfig = wmsTacticsConfigMapper.selectOne(tacticsQuery);
                    ////策略
                    //String tactics = wmsTacticsConfig.getTactics();
                    //if (TacticsEnum.CONCENTRATE.getCode().equals(tactics)) {//集中堆放策略
                    //    //剩余需分配托盘数
                    //    Long needNum = num;
                    //    //集中堆放的库区编码顺序
                    //    String tacticsContent = wmsTacticsConfig.getTacticsContent();
                    //    String[] areaCodeArrays = tacticsContent.split(",");
                    //    endLoop:
                    //    for (String areaCode : areaCodeArrays) {
                    //        for (AreaDto areaMap : areaMapList) {
                    //            if (areaCode.equals(areaMap.getAreaCode())) {
                    //                //找到该库区后，获取该库区空盘数量
                    //                Long emptyCount = areaMap.getEmptyNum();
                    //                if (needNum - emptyCount >= 0) {
                    //                    //该库区需空盘数为全部空盘
                    //                    areaMap.setSureCount(emptyCount);
                    //                    needNum -= emptyCount;
                    //                } else {
                    //                    //该库区需空盘数为needNum
                    //                    areaMap.setSureCount(needNum);
                    //                    //跳出整个循环
                    //                    break endLoop;
                    //                }
                    //            }
                    //        }
                    //    }
                    //} else {//平均分配策略
                    //    //获取各库区该货物编码所属托盘数量
                    //    List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), goodsCode);
                    //    for (AreaDto areaMap : areaMapList) {
                    //        String areaCode = areaMap.getAreaCode();
                    //        for (AreaDto m : goodsTrayCountByAreaType) {
                    //            if (areaCode.equals(m.getAreaCode())) {
                    //                areaMap.setGoodsCount(m.getGoodsCount());
                    //            }
                    //        }
                    //    }
                    //    //根据库区空托盘数及库区该类型托盘数分配需取的空托盘数
                    //    for (int i = 0; i < num; i++) {
                    //        //将库区list根据goodsCount正序排序
                    //        areaMapList = areaMapList.stream()
                    //                .sorted(Comparator.comparing(areaMap -> areaMap.getGoodsCount())).collect(Collectors.toList());
                    //        for (AreaDto areaMap : areaMapList) {
                    //            Long emptyCount = areaMap.getEmptyNum();//库区内的空托盘数量
                    //            if (emptyCount > 0) {//从该库区取一个空托盘
                    //                areaMap.setEmptyNum(emptyCount - 1L);
                    //                areaMap.setSureCount(areaMap.getSureCount() + 1);
                    //                areaMap.setGoodsCount(areaMap.getGoodsCount() + 1);
                    //                break;
                    //            } else {//去下一个库区取空托盘
                    //                continue;
                    //            }
                    //        }
                    //    }
                    //}


                    //平均分配策略,获取各库区该货物编码所属托盘数量
                    List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), goodsCode);
                    for (AreaDto areaMap : areaMapList) {
                        String areaCode = areaMap.getAreaCode();
                        for (AreaDto m : goodsTrayCountByAreaType) {
                            if (areaCode.equals(m.getAreaCode())) {
                                areaMap.setGoodsCount(m.getGoodsCount());
                            }
                        }
                    }
                    //根据库区空托盘数及库区该类型托盘数分配需取的空托盘数
                    for (int i = 0; i < num; i++) {
                        //将库区list根据goodsCount正序排序
                        areaMapList = areaMapList.stream()
                                .sorted(Comparator.comparing(areaMap -> areaMap.getGoodsCount())).collect(Collectors.toList());
                        for (AreaDto areaMap : areaMapList) {
                            Long emptyCount = areaMap.getEmptyNum();//库区内的空托盘数量
                            if (emptyCount > 0) {//从该库区取一个空托盘
                                areaMap.setEmptyNum(emptyCount - 1L);
                                areaMap.setSureCount(areaMap.getSureCount() + 1);
                                areaMap.setGoodsCount(areaMap.getGoodsCount() + 1);
                                break;
                            } else {//去下一个库区取空托盘
                                continue;
                            }
                        }
                    }
                }
                //组装数据，取盘
                List<WmsWcsInfo> infoList = new ArrayList<>();
                Map<Integer, WmsWcsInfo> infoMap = new HashMap<>();
                areaMapList = areaMapList.stream().sorted(Comparator.comparing(areaMap -> areaMap.getAreaCode())).collect(Collectors.toList());
                for (int i = 0; i < areaMapList.size(); i++) {
                    AreaDto areaMap = areaMapList.get(i);
                    if (areaMap.getSureCount() == 0) {
                        continue;
                    }
                    //trayCode 空托盘, locationCode 空托盘所在的库位
                    List<LocationMapVo> emptyTrayList = trayMapper.getEmptyTray(areaMap.getAreaCode(), Integer.parseInt(areaMap.getSureCount() + ""));

                    //锁定托盘所在库位
                    List<String> locationCodeList = emptyTrayList.stream().map(emptyTray -> emptyTray.getLocationCode()).collect(Collectors.toList());
                    LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                    locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                            .in(Location::getLocationCode, locationCodeList);
                    locationService.getBaseMapper().update(null, locationUpdate);
                    //交叠组装数据
                    for (int j = 0; j < emptyTrayList.size(); j++) {
                        LocationMapVo emptyTray = emptyTrayList.get(j);
                        if(emptyTray.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())){
                            WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                            info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                            info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                            info.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                            info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                            info.put(WmsWcsInfo.DOC, inbillCode);
                            infoMap.put(i + size * j, info);
                            hasParentLocationCodes.add(emptyTray.getLocationCode());
                        }else{
                            Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                                    .eq("enable_status", EnableStatus.ENABLE.getCode())
                                    .eq("location_code", emptyTray.getLocationCode())
                            );
                            Location parentLocation = locationService.getParentLocation(childLocation, areaMap.getAreaCode());

                            //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                            if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                                WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                                info.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                                info.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                                info.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                                info.put(WmsWcsInfo.DOC, inbillCode);
                                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                                infoMap.put(i + size * j, info);
                            }else{
                                String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(areaMap.getAreaCode());

                                List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                                WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                                info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                info.put(WmsWcsInfo.DOC, inbillCode);
                                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);

                                //1.母库位移动至移库库位
                                WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                                childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());

                                childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                                childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                                childInfo.put(WmsWcsInfo.DOC, inbillCode);
                                childInfoList.add(childInfo);

                                //2.取盘具体任务
                                WmsWcsInfo moveInfo = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode());
                                moveInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                                moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                                moveInfo.put(WmsWcsInfo.START_AREA_CODE, areaMap.getAreaCode());
                                moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                                moveInfo.put(WmsWcsInfo.DOC, inbillCode);
                                childInfoList.add(moveInfo);


                                //移库库位回至母库位
                                WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                                childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                                childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                                childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                                childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                                childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                                childInfo3.put(WmsWcsInfo.DOC, inbillCode);
                                childInfoList.add(childInfo3);

                                info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                                infoMap.put(i + size * j, info);
                            }
                        }
                    }
                }
                Set<Integer> integers = infoMap.keySet();
                List<Integer> collect = integers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                for (Integer integer : collect) {
                    infoList.add(infoMap.get(integer));
                }
                //取盘
                String msg = waittaskService.takeTray(infoList);
                //增加入库详情取盘数量
                trayMapper.updateTakedTrayCount(num.intValue(), inbillDetailId);
                WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(true, false);
            }).start();
        } catch (Exception e) {
            WmsTaskConstant.TAKE_TRAY_TASK.compareAndSet(true, false);
        }
        return AjaxResult.success("智能取盘任务下发成功");
    }


    /**
     * 回盘(理货区->存储区)
     *
     * @param map （trayCodeList托盘编码集合）
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult putTray(TrayDto map) {
        if (StringUtils.isNull(map)) {
            throw new ServiceException("回盘时的参数不能为空");
        }
        List<LocationMapVo> trayInfoList = null;
        //需要回盘的托盘编码
        List<String> trayCodeList = map.getTrayCodeList();
        if (trayCodeList.isEmpty()) {
            throw new ServiceException("回盘时的托盘信息不能为空");
        }
        //将托盘关联入库单中未上架的数据修改为上架中
        if (WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(map.getTaskType())) {
            //校验托盘的合法性
            validteTrayCode(trayCodeList);

            LambdaQueryWrapper<ListingDetail> listingDetailQuery = Wrappers.lambdaQuery();
            listingDetailQuery.select(ListingDetail::getTrayCode)
                    .eq(ListingDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(ListingDetail::getListingStatus, ListingEnum.NOT.getCode())
                    .in(ListingDetail::getTrayCode, trayCodeList);
            List<ListingDetail> datas = listingDetailMapper.selectList(listingDetailQuery);
            List<String> trayCodes = datas.stream().map(nolistWait -> nolistWait.getTrayCode()).distinct().collect(Collectors.toList());
            long count = trayCodes.size();
            if (count < trayCodeList.size()) {
                throw new ServiceException("存在上架任务中不涉及的托盘，请使用对应的回盘功能");
            }
            LambdaUpdateWrapper<ListingDetail> listingDetailUpdate = Wrappers.lambdaUpdate();
            listingDetailUpdate.set(ListingDetail::getListingStatus, ListingEnum.ING.getCode())
                    .eq(ListingDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(ListingDetail::getListingStatus, ListingEnum.NOT.getCode())
                    .in(ListingDetail::getTrayCode, trayCodeList);
            listingDetailMapper.update(null, listingDetailUpdate);
        } else if (WmsWcsTaskTypeEnum.NO_ORDER.getCode().equals(map.getTaskType())) {
            //校验托盘的合法性
            validteTrayCode(trayCodeList);
            LambdaQueryWrapper<NolistWait> nolistWaitLambdaQueryWrapper = Wrappers.lambdaQuery();
            nolistWaitLambdaQueryWrapper.select(NolistWait::getTrayCode)
                    .in(NolistWait::getTrayCode, trayCodeList)
                    .eq(NolistWait::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            List<NolistWait> datas = nolistWaitMapper.selectList(nolistWaitLambdaQueryWrapper);
            List<String> trayCodes = datas.stream().map(nolistWait -> nolistWait.getTrayCode()).distinct().collect(Collectors.toList());
            long count = trayCodes.size();
            if (count < trayCodeList.size()) {
                throw new ServiceException("存在无单任务中不涉及的托盘，请使用对应的回盘功能");
            }
            LambdaUpdateWrapper<NolistWait> nolistWaitLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            nolistWaitLambdaUpdateWrapper.set(NolistWait::getListingStatus, ListingEnum.ING.getCode())
                    .in(NolistWait::getTrayCode, trayCodeList).eq(NolistWait::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            nolistWaitMapper.update(null, nolistWaitLambdaUpdateWrapper);
        } else if (WmsWcsTaskTypeEnum.NORMAL_EMPTYTRAY.getCode().equals(map.getTaskType())) {
            //获取理货区agv是否启用
            LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
            wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, map.getStartAreaCode())
                    .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                    .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
            if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {
                List<Location> locationList = map.getLocationList();
                if(ObjectUtil.isEmpty(locationList)){
                    throw new ServiceException("未指定理货区库位！");
                }
                long count = locationList.stream().map(Location::getLocationCode).distinct().count();
                if(count < locationList.size()){
                    throw new ServiceException("理货区库位存在重复！");
                }
                List<String> trayCodes = locationList.stream().map(Location::getTrayCode).collect(Collectors.toList());
                //根据托盘号查询库位
                LambdaQueryWrapper<Location> locationQueryWrapper = Wrappers.lambdaQuery();
                locationQueryWrapper.in(Location::getTrayCode, trayCodes)
                        .eq(Location::getAreaId, "LHQ01");
                List<Location> locations = locationMapper.selectList(locationQueryWrapper);
                if (ObjectUtil.isNotEmpty(locations)) {
                    //修改该库位托盘号
                    for (Location location : locations) {
                        location.setTrayCode(null);
                    }
                    //情况该托盘原库位的托盘
                    locationService.updateBatchById(locations);
                }
                //修改该库位托盘号
                for (Location location : locationList) {
                    UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("tray_code",location.getTrayCode());
                    updateWrapper.eq("location_code",location.getLocationCode());
                    locationMapper.update(null,updateWrapper);
                }
            }
            //校验托盘的合法性
            validteTrayCode(trayCodeList);
            List<Tray> emptyTrayList = trayMapper.havEmptyTray(trayCodeList);
            if (emptyTrayList.size() < trayCodeList.size()) {
                throw new ServiceException("该回盘只能回空托盘，您选择的托盘包含了非空托盘");
            }
        } else if (WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode().equals(map.getTaskType())) {
            throw new ServiceException("存在移库任务中不涉及的托盘，请使用对应的回盘功能");
        } else if (WmsWcsTaskTypeEnum.DRY_STORAGE.getCode().equals(map.getTaskType())) {
            throw new ServiceException("存在晾晒入库任务中不涉及的托盘，请使用对应的回盘功能");
        } else if (WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(map.getTaskType())) {
            //校验托盘的合法性
            validteTrayCode(trayCodeList);
            LambdaQueryWrapper<OutbillGoods> outbillGoodsLambdaQueryWrapper = Wrappers.lambdaQuery();
            outbillGoodsLambdaQueryWrapper.select(OutbillGoods::getTrayCode)
                    .in(OutbillGoods::getTrayCode, trayCodeList)
                    .eq(OutbillGoods::getOutBillStatus, OutBillStatusEnum.OUTPROCESS.getCode())
                    .eq(OutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode());
            List<OutbillGoods> outbillGoods = outbillGoodsMapper.selectList(outbillGoodsLambdaQueryWrapper);
            List<String> trayCodes = outbillGoods.stream().map(outbillGoods1 -> outbillGoods1.getTrayCode()).distinct().collect(Collectors.toList());
            if (trayCodes.size() < trayCodeList.size()) {
                throw new ServiceException("存在出库任务中不涉及的托盘，请使用对应的回盘功能");
            }
        }
        List<WmsWcsInfo> wmsWcsInfoList = new ArrayList<>();
        //获取理货区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, map.getStartAreaCode())
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        // agv启用
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {
            if (StringUtils.isNull(map.getStartAreaCode())) {
                throw new ServiceException("回盘时的操作库区不能为空");
            }
            //托盘信息（获取起点库位）
            trayInfoList = trayMapper.getTrayInfoInCode(map.getStartAreaCode(), trayCodeList);
            if (trayInfoList.size() < trayCodeList.size()) {
                List<String> s = new ArrayList<>();
                for (String trayCode : trayCodeList) {
                    for (LocationMapVo m : trayInfoList) {
                        if (trayCode.equals(m.getTrayCode())) {
                            s.add(trayCode);
                        }
                    }
                }
                List<String> notFind = trayCodeList.stream().filter(code -> !s.contains(code)).collect(Collectors.toList());
                throw new ServiceException("托盘【" + notFind + "】在理货区找不到");
            }
            wmsWcsInfoList = putTrayByAGV(WmsWcsTypeEnum.PUTTRAY.getCode(), trayInfoList, map.getTaskType(),map.getDoc());
        } else {
            //人工回盘
            if (StringUtils.isEmpty(map.getEndAreaCode())) {
                throw new ServiceException("人工回盘必须要有回归库区");
            }
            trayInfoList = new ArrayList<>();
            for (String trayCode : trayCodeList) {
                LocationMapVo map1 = new LocationMapVo();
                map1.setTrayCode(trayCode);
                map1.setEndAreaCode(map.getEndAreaCode());
                trayInfoList.add(map1);
            }
            wmsWcsInfoList = peoplePutTray(WmsWcsTypeEnum.PUTTRAY.getCode(), trayInfoList, map.getEndAreaCode(), map.getTaskType(),map.getDoc());
        }
        if(ObjectUtil.isEmpty(wmsWcsInfoList)){
            throw new ServiceException("当前任务不可提交，堆垛机出现故障！");
        }
        //记录给wcs任务的数据
        tasklogService.saveBatch(wmsWcsInfoList);
        //把任务的数据给wcs
        List<WmsToWcsTaskReq> collect = wmsWcsInfoList.stream().map(info -> {
            String jsonStr = JSONObject.toJSONString(info);
            return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
        }).collect(Collectors.toList());
        new Thread(() -> {
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }).start();
        return AjaxResult.success("成功", wmsWcsInfoList);
    }


    /**
     * 校验托盘的合法性
     * @param trayCodeList
     */
    public void validteTrayCode(List<String> trayCodeList){

        //判断上架的托盘，在基础信息中是否维护过
        boolean isExistsStoreAreaTray = trayMapper.selectCount(new QueryWrapper<Tray>()
                .in("tray_code", trayCodeList)
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
        ).intValue() == trayCodeList.size();
        if(!isExistsStoreAreaTray){
            throw new ServiceException("上架的托盘信息，未在系统中查询到");
        }


        //判断上架托盘，是否是已经 不在存储的托盘
        List<Location> locationInTrayList = locationService.getBaseMapper().selectList(new QueryWrapper<Location>()
                .in("tray_code", trayCodeList)
                .in("area_id", areaMapper.selectAreaCodeByType(AreaTypeEnum.CCQ.getCode()))
                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("lock_status", LockEnum.NOTLOCK.getCode())
        );

        if(CollUtil.isNotEmpty(locationInTrayList)){
            String inTrayCodes = locationInTrayList.parallelStream().map(Location::getTrayCode).collect(Collectors.joining(","));
            throw new ServiceException("无法上架，托盘:[ "+ inTrayCodes + "]不在理货区");
        }
    }
    /**
     * 人工回盘
     *
     * @param wcsType      操作类型
     * @param trayInfoList 托盘信息(数据库记录的位置等)
     * @param areaCode     存储区-库区编码（有就查询对应库区，没有查全部）
     * @param taskType     任务类型()
     */
    public List<WmsWcsInfo> peoplePutTray(String wcsType, List<LocationMapVo> trayInfoList, String areaCode, String taskType,String doc) {
        List<WmsWcsInfo> wmsWcsInfoList = new ArrayList<>();
        List<String> hasParentLocationCodes = new ArrayList<>();
        //获取存储区的空库位
        //1、获取存储区库区以及空库位数量
        List<AreaDto> areaLists = areaMapper.selectAllAreaByType(AreaTypeEnum.CCQ.getCode(), areaCode);
        if (areaLists.isEmpty() || (trayInfoList.size() > (areaLists.get(0).getEmptyNum()))) {
            throw new ServiceException("该库区的空库位数量不满足存放传入的托盘数量");
        }
        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，回盘失败");
        }
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        List<AreaDto> stackerOnline = areaLists.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(stackerOnline)) {
            throw new ServiceException(areaCode + "堆垛机目前处于手动模式，回盘失败");
        }

        //2、分配库区（即每个库区要放多少托盘）
        int num = trayInfoList.size();
        endFor:
        for (int i = 0; i < trayInfoList.size(); i++) {
            for (AreaDto areaMap : areaLists) {
                if (num == 0) {
                    break endFor;
                }
                long emptyNum = areaMap.getEmptyNum();//空库位数量
                if (emptyNum > 0) {
                    //表示从该库区取一个空库位
                    areaMap.setEmptyNum(emptyNum--);
                    areaMap.setSureCount(areaMap.getSureCount() + 1);
                    num--;
                }
            }
        }
        //3、取实际的空库位编码（锁定该库位）
        for (AreaDto areaMap : areaLists) {
            if (areaMap.getSureCount() > 0) {
                //根据库区以及所需空库位的数量，返回要回如的库位编码
                List<String> sureLocations = locationService.getBaseMapper().getEmptyLocation(areaMap.getAreaCode(), Integer.parseInt(areaMap.getSureCount() + ""))
                        .stream()
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
                areaMap.setSureLocations(sureLocations);
                if (sureLocations.size() < areaMap.getSureCount()) {
                    throw new ServiceException("存储区中编码为" + areaMap.getAreaCode() + "的库区里空库位不足");
                }
                //锁定库位
                int i = locationService.getBaseMapper().lockLocation(sureLocations);
                if (i < sureLocations.size()) {
                    throw new ServiceException("分配的部分库位已被占用，请重试");
                }
                for (String location : sureLocations) {
                    for (LocationMapVo obj : trayInfoList) {
                        if (StringUtils.isNull(obj.getEndLocationCode())) {
                            obj.setEndLocationCode(location);
                            obj.setEndAreaCode(areaMap.getAreaCode());
                            break;
                        }
                    }
                }
            }
        }
        //给托盘分配库位并发给wcs
        for (int i = 0; i < trayInfoList.size(); i++) {
            LocationMapVo emptyTray = trayInfoList.get(i);
            Location targetLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("enable_status", EnableStatus.ENABLE.getCode())
                    .eq("location_code", emptyTray.getEndLocationCode())
            );

            if(targetLocation.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())) {
                WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                if(StrUtil.isNotEmpty(doc)){
                    wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                }
                wmsWcsInfoList.add(wmsWcsInfo);
            }else {
                Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("enable_status", EnableStatus.ENABLE.getCode())
                        .eq("location_code", emptyTray.getEndLocationCode())
                );
                Location parentLocation = locationService.getParentLocation(childLocation, emptyTray.getEndAreaCode());
                if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null) {
                    WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                    wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                    wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                    if(StrUtil.isNotEmpty(doc)){
                        wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    wmsWcsInfoList.add(wmsWcsInfo);
                }else{
                    String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(emptyTray.getEndAreaCode());

                    List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                    WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    info.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());

                    //1.母库位移动至移库库位
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                    if(StrUtil.isNotEmpty(doc)){
                        childInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(childInfo);

                    //2.回盘具体任务
                    WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                    wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                    wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                    if(StrUtil.isNotEmpty(doc)){
                        wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(wmsWcsInfo);


                    //移库库位回至母库位
                    WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                    childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                    if(StrUtil.isNotEmpty(doc)){
                        childInfo3.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(childInfo3);
                    info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                    wmsWcsInfoList.add(info);
                }
            }
        }
        return wmsWcsInfoList;
    }


    /**
     * AGV回盘
     *
     * @param wcsType      操作类型
     * @param trayInfoList 托盘信息(数据库记录的位置等)
     * @param taskType     任务类型
     * @return
     */
    public List<WmsWcsInfo> putTrayByAGV(String wcsType, List<LocationMapVo> trayInfoList, String taskType,String doc) {
        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
        }
        //获取存储区的空库位
        //1、获取存储区库区以及空库位数量
        List<AreaDto> areaMapList = areaMapper.selectAllAreaByType(AreaTypeEnum.CCQ.getCode(), null);
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        List<AreaDto> areaDtos = areaMapList.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());
        List<AreaDto> areaLists = areaDtos;

        List<WmsWcsInfo> wmsWcsInfoList = new ArrayList<>();
        List<String> hasParentLocationCodes = new ArrayList<>();
        //1、获取全局策略
        LambdaQueryWrapper<WmsTacticsConfig> tacticsQuery = Wrappers.lambdaQuery();
        tacticsQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .last(" limit 1");
        WmsTacticsConfig wmsTacticsConfig = wmsTacticsConfigMapper.selectOne(tacticsQuery);
        //策略
        String tactics = wmsTacticsConfig.getTactics();

        //如果上架/回盘/托盘回收
        if (WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(taskType) ||
                WmsWcsTaskTypeEnum.NORMAL_EMPTYTRAY.getCode().equals(taskType) ||
                WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode().equals(taskType)
        ) {

            //CCQ01托盘信息
            List<LocationMapVo> ccq01Locations = new ArrayList<>();
            //CCQ02、CCQ03托盘信息
            List<LocationMapVo> ccq023Locations = new ArrayList<>();
            for (LocationMapVo location : trayInfoList) {
                String locationCode = location.getLocationCode();
                for (String stagingOne : LhqLocationConstants.stagingOnes) {
                    if (locationCode.equals(stagingOne)) {
                        ccq01Locations.add(location);
                    }
                }
                for (String stagingTwo : LhqLocationConstants.stagingTwos) {
                    if (locationCode.equals(stagingTwo)) {
                        ccq023Locations.add(location);
                    }
                }
            }
            //理货区库位排序
            ccq023Locations =  ccq023Locations.stream()
                    .sorted(Comparator.comparing(LocationMapVo::getOrderNum))
                    .collect(Collectors.toList());

            // 根据顺序进行逆序排序
            ccq01Locations = ccq01Locations.stream()
                    .sorted(Comparator.comparing(LocationMapVo::getOrderNum).reversed())
                    .collect(Collectors.toList());

            List<LocationMapVo> tempTrayInfoList = new ArrayList<>(ccq023Locations);
            tempTrayInfoList.addAll(ccq01Locations);
            trayInfoList = tempTrayInfoList;

            if (trayInfoList.size() != ccq023Locations.size() + ccq01Locations.size()) {
                throw new ServiceException("有托盘未在理货区");
            }
            //获取对应存储区库区以及空库位数量
            List<AreaDto> locationCodeListOne = new ArrayList<>();
            List<AreaDto> locationCodeListTwo = new ArrayList<>();
            locationCodeListOne = areaLists.parallelStream().filter(item -> "CCQ01".contains(item.getAreaCode())).collect(Collectors.toList());
            locationCodeListTwo = areaLists.parallelStream().filter(item -> !"CCQ01".contains(item.getAreaCode())).collect(Collectors.toList());

            //CCQ01固定
            if(ccq01Locations.size()>0){
                //所需库位数量
                int sureCount = ccq01Locations.size();
                for (AreaDto areaMap : locationCodeListOne) {
                    //分配库区（即每个库区要放多少托盘）
                    int emptyNum = Integer.parseInt(areaMap.getEmptyNum() + "");//空库位数量
                    if (emptyNum >= sureCount) {//空库位数大于等于需要数量
                        areaMap.setSureCount(Long.valueOf(sureCount + ""));
                        areaMap.setEmptyNum(Long.valueOf(emptyNum - sureCount));
                        sureCount = 0;
                    } else {
                        areaMap.setSureCount(Long.valueOf(emptyNum + ""));
                        areaMap.setEmptyNum(0L);
                        sureCount = sureCount - emptyNum;
                    }
                }
                if (sureCount > 0) {
                    throw new ServiceException("存储区1区摆放此次托盘需要的存储区空库位数量不足");
                }
            }
            //集中堆放
            if (TacticsEnum.CONCENTRATE.getCode().equals(tactics)) {
                //2、按照配置的库区排序开始占用库位
                String tacticsContent = wmsTacticsConfig.getTacticsContent();
                String[] tacticsContents = Arrays.stream(tacticsContent.split(","))
                        .filter(content -> !content.equals("CCQ01"))
                        .toArray(String[]::new);
                //所需库位数量
                int sureCount = ccq023Locations.size();
                //根据库区顺序分配空库位
                endLoop:
                for (int j = 0; j < tacticsContents.length; j++) {
                    if (sureCount == 0) {
                        break;
                    }
                    for (AreaDto areaMap : locationCodeListTwo) {
                        if (areaMap.getAreaCode().equals(tacticsContents[j])) {
                            //分配库区（即每个库区要放多少托盘）
                            int emptyNum = Integer.parseInt(areaMap.getEmptyNum() + "");//空库位数量
                            if (emptyNum >= sureCount) {//空库位数大于等于需要数量
                                areaMap.setSureCount(Long.valueOf(sureCount + ""));
                                areaMap.setEmptyNum(Long.valueOf(emptyNum - sureCount));
                                sureCount = 0;
                                break endLoop;
                            } else {
                                areaMap.setSureCount(Long.valueOf(emptyNum + ""));
                                areaMap.setEmptyNum(0L);
                                sureCount = sureCount - emptyNum;
                            }
                        }
                    }
                }
                if (sureCount > 0) {
                    throw new ServiceException(Arrays.toString(tacticsContents) +"摆放此次托盘需要的存储区空库位数量不足");
                }
            } else if (TacticsEnum.AVERAGE.getCode().equals(tactics)) {    //平均分配
                //根据是否空盘分开托盘信息
                List<LocationMapVo> emptyTray = ccq023Locations.stream().filter(tray -> StrUtil.isEmpty(tray.getGoodsCode())).collect(Collectors.toList());
                List<LocationMapVo> notEmptyTray = ccq023Locations.stream().filter(tray -> StrUtil.isNotEmpty(tray.getGoodsCode())).collect(Collectors.toList());
                //空盘分配库位,获取每个库区下同类型货物托盘数量，从最小值开始占用库位
                if (emptyTray.size() > 0) {
                    List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), "");
                    goodsTrayCountByAreaType = goodsTrayCountByAreaType.parallelStream().filter(item -> !"CCQ01".contains(item.getAreaCode())).collect(Collectors.toList());
                    //获取各库区该货物编码所属托盘数量
                    for (AreaDto map : locationCodeListTwo) {
                        for (AreaDto areaDto : goodsTrayCountByAreaType) {
                            if (map.getAreaCode().equals(areaDto.getAreaCode())) {
                                map.setGoodsCount(areaDto.getGoodsCount());
                            }
                        }
                    }

                    for (int j = 0; j < emptyTray.size(); j++) {
                        locationCodeListTwo = locationCodeListTwo.stream()
                                .sorted(Comparator.comparing(AreaDto::getGoodsCount, Comparator.naturalOrder()).thenComparing(AreaDto::getEmptyNum, Comparator.reverseOrder())).collect(Collectors.toList());
                        for (AreaDto areaMap : locationCodeListTwo) {
                            long emptyNum = areaMap.getEmptyNum();//空库位数量
                            if (emptyNum > 0) {
                                //表示从该库区取一个空库位
                                areaMap.setEmptyNum(emptyNum - 1L);
                                areaMap.setSureCount(areaMap.getSureCount() + 1L);
                                areaMap.setGoodsCount(areaMap.getGoodsCount() + 1L);
                                break;
                            }
                        }
                    }
                }
                //非空盘分配（货物分类），获取每个库区下同类型货物托盘数量，从最小值开始占用库位
                if (notEmptyTray.size() > 0) {
                    List<String> goodsCodes = notEmptyTray.stream().map(tray -> tray.getGoodsCode()).distinct().collect(Collectors.toList());
                    for (String goodsCode : goodsCodes) {
                        //获取各库区该货物编码所属托盘数量
                        List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), goodsCode);
                        goodsTrayCountByAreaType = goodsTrayCountByAreaType.parallelStream().filter(item -> !"CCQ01".contains(item.getAreaCode())).collect(Collectors.toList());
                        for (AreaDto map : locationCodeListTwo) {
                            for (AreaDto areaDto : goodsTrayCountByAreaType) {
                                if (map.getAreaCode().equals(areaDto.getAreaCode())) {
                                    map.setGoodsCount(areaDto.getGoodsCount());
                                }
                            }
                        }
                        for (int j = 0; j < notEmptyTray.size(); j++) {
                            if (notEmptyTray.get(j).getGoodsCode().equals(goodsCode)) {
                                locationCodeListTwo = locationCodeListTwo.stream()
                                        .sorted(Comparator.comparing(AreaDto::getGoodsCount, Comparator.naturalOrder()).thenComparing(AreaDto::getEmptyNum, Comparator.reverseOrder())).collect(Collectors.toList());
                                for (AreaDto areaMap : locationCodeListTwo) {
                                    long emptyNum = areaMap.getEmptyNum();//空库位数量
                                    if (emptyNum > 0) {
                                        //表示从该库区取一个空库位
                                        areaMap.setEmptyNum(emptyNum - 1L);
                                        areaMap.setSureCount(areaMap.getSureCount() + 1L);
                                        areaMap.setGoodsCount(areaMap.getGoodsCount() + 1L);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                locationCodeListTwo = locationCodeListTwo.stream().sorted(Comparator.comparing(item -> item.getAreaCode())).collect(Collectors.toList());
            }
            if(ccq01Locations.size()>0){
                //3、取实际的CCQ01空库位编码（锁定该库位）
                agvEndArea(locationCodeListOne,ccq01Locations);
            }
            if(ccq023Locations.size()>0){
                //3、取实际的CCQ02、CCQ03空库位编码（锁定该库位）
                agvEndArea(locationCodeListTwo,ccq023Locations);
            }
            //上架 修改未指定库位的托盘为未上架
            if(WmsWcsTaskTypeEnum.NORMAL_WAREHOUSING.getCode().equals(taskType)){
                List<LocationMapVo> collectLocationMapVo = ccq023Locations.stream()
                        .filter(locationMapVo -> locationMapVo.getEndAreaCode() == null)
                        .collect(Collectors.toList());
                List<String> collectTrayCode = collectLocationMapVo.stream().map(LocationMapVo::getTrayCode).collect(Collectors.toList());

                if(ObjectUtil.isNotEmpty(collectLocationMapVo)){
                    LambdaUpdateWrapper<ListingDetail> listingDetailUpdate = Wrappers.lambdaUpdate();
                    listingDetailUpdate.set(ListingDetail::getListingStatus, ListingEnum.NOT.getCode())
                            .eq(ListingDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                            .eq(ListingDetail::getListingStatus, ListingEnum.ING.getCode())
                            .in(ListingDetail::getTrayCode, collectTrayCode);
                    listingDetailMapper.update(null, listingDetailUpdate);
                }
            }

            if (TacticsEnum.AVERAGE.getCode().equals(tactics)) {
                ccq023Locations = ccq023Locations.stream()
                        .filter(locationMapVo -> locationMapVo.getEndAreaCode() != null)
                        .collect(Collectors.toList());

                Map<String, List<LocationMapVo>> collect = ccq023Locations.stream().collect(Collectors.groupingBy(LocationMapVo::getEndAreaCode));

                if(collect.size()>1){
                    List<String> filterSureLocations = new ArrayList<>();
                    for (int i = 0; i < ccq023Locations.size(); i++) {
                        int spare = i % collect.size();
                        List<String> areaSureLocations = locationCodeListTwo.get(spare).getSureLocations();
                        if (CollUtil.isNotEmpty(areaSureLocations)) {
                            boolean matched = false;
                            for (String areaOneSureLocation : areaSureLocations) {
                                if (!filterSureLocations.contains(areaOneSureLocation)) {
                                    ccq023Locations.get(i).setEndAreaCode(locationCodeListTwo.get(spare).getAreaCode());
                                    ccq023Locations.get(i).setEndLocationCode(areaOneSureLocation);
                                    filterSureLocations.add(areaOneSureLocation);
                                    matched = true;
                                    break;
                                }
                            }
                            if (!matched) {
                                int nextSpare = (spare + 1) % collect.size();
                                List<String> areaSureLocations1 = locationCodeListTwo.get(nextSpare).getSureLocations();
                                for (String areaOneSureLocation : areaSureLocations1) {
                                    if (!filterSureLocations.contains(areaOneSureLocation)) {
                                        ccq023Locations.get(i).setEndAreaCode(locationCodeListTwo.get(nextSpare).getAreaCode());
                                        ccq023Locations.get(i).setEndLocationCode(areaOneSureLocation);
                                        filterSureLocations.add(areaOneSureLocation);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            trayInfoList =  trayInfoList.stream()
                    .filter(locationMapVo -> locationMapVo.getEndAreaCode() != null)
                    .collect(Collectors.toList());
        }else {
            if (TacticsEnum.CONCENTRATE.getCode().equals(tactics)) {  //集中堆放
                //2、按照配置的库区排序开始占用库位
                String tacticsContent = wmsTacticsConfig.getTacticsContent();
                String[] tacticsContents = tacticsContent.split(",");
                //所需库位数量
                int sureCount = trayInfoList.size();
                //根据库区顺序分配空库位
                endLoop:
                for (int j = 0; j < tacticsContents.length; j++) {
                    if (sureCount == 0) {
                        break;
                    }
                    for (AreaDto areaMap : areaLists) {
                        if (areaMap.getAreaCode().equals(tacticsContents[j])) {
                            //分配库区（即每个库区要放多少托盘）
                            int emptyNum = Integer.parseInt(areaMap.getEmptyNum() + "");//空库位数量
                            if (emptyNum >= sureCount) {//空库位数大于等于需要数量
                                areaMap.setSureCount(Long.valueOf(sureCount + ""));
                                areaMap.setEmptyNum(Long.valueOf(emptyNum - sureCount));
                                sureCount = 0;
                                break endLoop;
                            } else {
                                areaMap.setSureCount(Long.valueOf(emptyNum + ""));
                                areaMap.setEmptyNum(0L);
                                sureCount = sureCount - emptyNum;
                            }
                        }
                    }
                }
                if (sureCount > 0) {
                    throw new ServiceException("摆放此次托盘需要的存储区空库位数量不足");
                }
            } else if (TacticsEnum.AVERAGE.getCode().equals(tactics)) {    //平均分配
                //根据是否空盘分开托盘信息
                List<LocationMapVo> emptyTray = trayInfoList.stream().filter(tray -> StrUtil.isEmpty(tray.getGoodsCode())).collect(Collectors.toList());
                List<LocationMapVo> notEmptyTray = trayInfoList.stream().filter(tray -> StrUtil.isNotEmpty(tray.getGoodsCode())).collect(Collectors.toList());
                //空盘分配库位,获取每个库区下同类型货物托盘数量，从最小值开始占用库位
                if (emptyTray.size() > 0) {
                    List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), "");
                    //获取各库区该货物编码所属托盘数量
                    for (AreaDto map : areaLists) {
                        for (AreaDto areaDto : goodsTrayCountByAreaType) {
                            if (map.getAreaCode().equals(areaDto.getAreaCode())) {
                                map.setGoodsCount(areaDto.getGoodsCount());
                            }
                        }
                    }

                    for (int j = 0; j < emptyTray.size(); j++) {
                        areaLists = areaLists.stream()
                                .sorted(Comparator.comparing(AreaDto::getGoodsCount, Comparator.naturalOrder()).thenComparing(AreaDto::getEmptyNum, Comparator.reverseOrder())).collect(Collectors.toList());
                        for (AreaDto areaMap : areaLists) {
                            long emptyNum = areaMap.getEmptyNum();//空库位数量
                            if (emptyNum > 0) {
                                //表示从该库区取一个空库位
                                areaMap.setEmptyNum(emptyNum - 1L);
                                areaMap.setSureCount(areaMap.getSureCount() + 1L);
                                areaMap.setGoodsCount(areaMap.getGoodsCount() + 1L);
                                break;
                            }
                        }
                    }
                }
                //非空盘分配（货物分类），获取每个库区下同类型货物托盘数量，从最小值开始占用库位
                if (notEmptyTray.size() > 0) {
                    List<String> goodsCodes = notEmptyTray.stream().map(tray -> tray.getGoodsCode()).distinct().collect(Collectors.toList());
                    for (String goodsCode : goodsCodes) {
                        //获取各库区该货物编码所属托盘数量
                        List<AreaDto> goodsTrayCountByAreaType = trayMapper.getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), goodsCode);
                        for (AreaDto map : areaLists) {
                            for (AreaDto areaDto : goodsTrayCountByAreaType) {
                                if (map.getAreaCode().equals(areaDto.getAreaCode())) {
                                    map.setGoodsCount(areaDto.getGoodsCount());
                                }
                            }
                        }
                        for (int j = 0; j < notEmptyTray.size(); j++) {
                            if (notEmptyTray.get(j).getGoodsCode().equals(goodsCode)) {
                                areaLists = areaLists.stream()
                                        .sorted(Comparator.comparing(AreaDto::getGoodsCount, Comparator.naturalOrder()).thenComparing(AreaDto::getEmptyNum, Comparator.reverseOrder())).collect(Collectors.toList());
                                for (AreaDto areaMap : areaLists) {
                                    long emptyNum = areaMap.getEmptyNum();//空库位数量
                                    if (emptyNum > 0) {
                                        //表示从该库区取一个空库位
                                        areaMap.setEmptyNum(emptyNum - 1L);
                                        areaMap.setSureCount(areaMap.getSureCount() + 1L);
                                        areaMap.setGoodsCount(areaMap.getGoodsCount() + 1L);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                areaLists = areaLists.stream().sorted(Comparator.comparing(item -> item.getAreaCode())).collect(Collectors.toList());
            }
//            agvEndArea(areaLists,trayInfoList);

            //start
            //3、取实际的空库位编码（锁定该库位）
            for (AreaDto areaMap : areaLists) {
                if (areaMap.getSureCount() > 0) {
                    //根据库区以及所需空库位的数量，返回要回如的库位编码
                    List<EmptyLocationBo> emptyLocationBos = locationService.getBaseMapper().getEmptyLocation(areaMap.getAreaCode(), Integer.parseInt(areaMap.getSureCount() + ""));
                    //子库位集合
                    List<String> childLocations = emptyLocationBos.stream()
                            .filter(item -> item.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode()))
                            .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());

                    //母库位集合
                    List<String> parentLocations = emptyLocationBos.stream()
                            .filter(item -> item.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode()))
                            .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());

                    //母库位和子库位合并(子库位在里，母库位在外)
                    List<String> sureLocations = new ArrayList<>();
                    sureLocations.addAll(childLocations);
                    sureLocations.addAll(parentLocations);


                    areaMap.setSureLocations(sureLocations);
                    if (sureLocations.size() < areaMap.getSureCount()) {
                        throw new ServiceException("存储区中编码为" + areaMap.getAreaCode() + "的库区里空库位不足");
                    }
                    //锁定库位
                    int i = locationService.getBaseMapper().lockLocation(sureLocations);
                    if (i < sureLocations.size()) {
                        throw new ServiceException("分配的部分库位已被占用，请重试");
                    }

                    //设置每个托盘需要放入 空库位的位置及库区
                    for (String location : sureLocations) {
                        for (LocationMapVo obj : trayInfoList) {
                            if (StringUtils.isNull(obj.getEndLocationCode())) {
                                obj.setEndLocationCode(location);
                                obj.setEndAreaCode(areaMap.getAreaCode());
                                break;
                            }
                        }
                    }
                }
            }
            //end
            if (TacticsEnum.AVERAGE.getCode().equals(tactics)) {
                trayInfoList =  trayInfoList.stream()
                        .filter(locationMapVo -> locationMapVo.getEndAreaCode() != null)
                        .collect(Collectors.toList());

                Map<String, List<LocationMapVo>> collect = trayInfoList.stream().collect(Collectors.groupingBy(LocationMapVo::getEndAreaCode));

                List<Integer> locationAreaSize =  new ArrayList<>();
                for (String s : collect.keySet()) {
                    locationAreaSize.add(collect.get(s).size());
                }

                List<String> filterSureLocations = new ArrayList<>();
                for (int i = 0; i < trayInfoList.size(); i++) {
                    int spare = i%collect.size();
                    List<String> areaSureLocations = areaDtos.get(spare).getSureLocations();
                    if(CollUtil.isNotEmpty(areaSureLocations)){
                        for (String areaOneSureLocation : areaSureLocations) {
                            if(!filterSureLocations.contains(areaOneSureLocation)){
                                trayInfoList.get(i).setEndAreaCode(areaDtos.get(spare).getAreaCode());
                                trayInfoList.get(i).setEndLocationCode(areaOneSureLocation);
                                filterSureLocations.add(areaOneSureLocation);
                                break;
                            }
                        }
                    }
                }
            }
        }
        //给托盘分配库位并发给wcs
        for (int i = 0; i < trayInfoList.size(); i++) {
            LocationMapVo emptyTray = trayInfoList.get(i);
            Location targetLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("enable_status", EnableStatus.ENABLE.getCode())
                    .eq("location_code", emptyTray.getEndLocationCode())
            );

            if(targetLocation.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())) {
                WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                if(StrUtil.isNotEmpty(doc)){
                    wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                }
                wmsWcsInfoList.add(wmsWcsInfo);
            }else {
                Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("enable_status", EnableStatus.ENABLE.getCode())
                        .eq("location_code", emptyTray.getEndLocationCode())
                );
                Location parentLocation = locationService.getParentLocation(childLocation, emptyTray.getEndAreaCode());
                if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null) {
                    WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                    wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                    wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                    if(StrUtil.isNotEmpty(doc)){
                        wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    wmsWcsInfoList.add(wmsWcsInfo);
                }else{
                    String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(emptyTray.getEndAreaCode());

                    List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                    WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    info.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());

                    //1.母库位移动至移库库位
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                    if(StrUtil.isNotEmpty(doc)){
                        childInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(childInfo);

                    //2.回盘具体任务
                    WmsWcsInfo wmsWcsInfo = WmsWcsInfo.getInfo(wcsType, taskType);
                    wmsWcsInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LHQ.getCode());
                    wmsWcsInfo.put(WmsWcsInfo.TRAY_CODE, emptyTray.getTrayCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_LOCATION_CODE, emptyTray.getLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.START_AREA_CODE, emptyTray.getAreaCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyTray.getEndLocationCode());
                    wmsWcsInfo.put(WmsWcsInfo.END_AREA_CODE, emptyTray.getEndAreaCode());
                    if(StrUtil.isNotEmpty(doc)){
                        wmsWcsInfo.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(wmsWcsInfo);


                    //移库库位回至母库位
                    WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                    childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                    if(StrUtil.isNotEmpty(doc)){
                        childInfo3.put(WmsWcsInfo.DOC, doc);
                    }
                    childInfoList.add(childInfo3);
                    info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                    wmsWcsInfoList.add(info);
                }
            }
        }
        return wmsWcsInfoList;
    }

    //回盘 锁定结束库位
    private void agvEndArea(List<AreaDto> areaLists,List<LocationMapVo> trayInfoList){
        //3、取实际的空库位编码（锁定该库位）
        for (AreaDto areaMap : areaLists) {
            if (areaMap.getSureCount() > 0) {
                //根据库区以及所需空库位的数量，返回要回如的库位编码
                List<EmptyLocationBo> emptyLocationBos = locationService.getBaseMapper().getEmptyLocation(areaMap.getAreaCode(), Integer.parseInt(areaMap.getSureCount() + ""));
                //子库位集合
                List<String> childLocations = emptyLocationBos.stream()
                        .filter(item -> item.getLocationType().equals(LocationTypeEnum.CHILD_LOCATION_TYPE.getCode()))
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());

                //母库位集合
                List<String> parentLocations = emptyLocationBos.stream()
                        .filter(item -> item.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode()))
                        .map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());

                //母库位和子库位合并(子库位在里，母库位在外)
                List<String> sureLocations = new ArrayList<>();
                sureLocations.addAll(childLocations);
                sureLocations.addAll(parentLocations);


                areaMap.setSureLocations(sureLocations);
                if (sureLocations.size() < areaMap.getSureCount()) {
                    throw new ServiceException("存储区中编码为" + areaMap.getAreaCode() + "的库区里空库位不足");
                }
                //锁定库位
                int i = locationService.getBaseMapper().lockLocation(sureLocations);
                if (i < sureLocations.size()) {
                    throw new ServiceException("分配的部分库位已被占用，请重试");
                }

                //设置每个托盘需要放入 空库位的位置及库区
                for (String location : sureLocations) {
                    for (LocationMapVo obj : trayInfoList) {
                        if (StringUtils.isNull(obj.getEndLocationCode())) {
                            obj.setEndLocationCode(location);
                            obj.setEndAreaCode(areaMap.getAreaCode());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询agv启用状态
     *
     * @param wcsDeviceBaseInfo
     * @return
     */
    public AjaxResult selectAgv(WcsDeviceBaseInfo wcsDeviceBaseInfo) {
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.select(WcsDeviceBaseInfo::getWarehouseAreaCode, WcsDeviceBaseInfo::getDeviceNo, WcsDeviceBaseInfo::getEnableStatus)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getDeviceArea, wcsDeviceBaseInfo.getDeviceArea());
        List<WcsDeviceBaseInfo> wcsDeviceBaseInfos = wcsDeviceBaseInfoMapper.selectList(wcsDeviceBaseInfoQuery);
        return AjaxResult.success("成功", wcsDeviceBaseInfos);
    }

}
