package com.ruoyi.wms.stock.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.domain.WmsToWcsTaskReq;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wms.basics.bo.DryInBillAgvBo;
import com.ruoyi.wms.basics.bo.EmptyLocationBo;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.basics.dto.AreaDto;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.mapper.WmsTacticsConfigMapper;
import com.ruoyi.wms.basics.service.AreaService;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.enums.AreaTypeEnum;
import com.ruoyi.wms.enums.DryInbillStatusEnum;
import com.ruoyi.wms.enums.LocationTypeEnum;
import com.ruoyi.wms.enums.TacticsEnum;
import com.ruoyi.wms.stock.domain.DryOutbillGoods;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.domain.WmsDryInbillGoods;
import com.ruoyi.wms.stock.dto.DryInbillGroupDiskDto;
import com.ruoyi.wms.stock.dto.DryInbillPutOnDto;
import com.ruoyi.wms.stock.mapper.DryOutbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillGoodsMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillMapper;
import com.ruoyi.wms.stock.vo.DryInBillTrayVo;
import com.ruoyi.wms.utils.constant.DryLocationConstants;
import com.ruoyi.wms.wcstask.service.TasklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 晾晒出入库单货物Service接口
 *
 * @author nf
 * @date 2023-03-10
 */
@Slf4j
@Service
public class WmsDryInbillGoodsService extends ServiceImpl<WmsDryInbillGoodsMapper, WmsDryInbillGoods> {

    @Autowired
    private WmsDryInbillGoodsMapper wmsDryInbillGoodsMapper;

    @Autowired
    private WmsDryInbillMapper wmsDryInbillMapper;

    @Autowired
    private DryOutbillGoodsMapper wmsDryOutbillGoodsMapper;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private TrayMapper trayMapper;

    @Autowired
    private LocationService locationService;

    @Autowired(required = false)
    private WmsTacticsConfigMapper wmsTacticsConfigMapper;

    @Autowired
    private TasklogService tasklogService;

    @Autowired(required = false)
    private WcsTaskApiService wcsTaskApiService;

    @Autowired
    protected Validator validator;

    @Autowired
    private AreaService areaService;

    /**
     * 查询晾晒出入库单货物
     *
     * @param id 晾晒出入库单货物主键
     * @return 晾晒出入库单货物
     */
    public WmsDryInbillGoods selectWmsDryInbillGoodsById(String id) {
        QueryWrapper<WmsDryInbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsDryInbillGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询晾晒出入库单货物
     *
     * @param ids 晾晒出入库单货物 IDs
     * @return 晾晒出入库单货物
     */
    public List<WmsDryInbillGoods> selectWmsDryInbillGoodsByIds(String[] ids) {
        QueryWrapper<WmsDryInbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsDryInbillGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询晾晒出入库单货物列表
     *
     * @param wmsDryInbillGoods 晾晒出入库单货物
     * @return 晾晒出入库单货物集合
     */
    public List<WmsDryInbillGoods> selectWmsDryInbillGoodsList(WmsDryInbillGoods wmsDryInbillGoods) {
        QueryWrapper<WmsDryInbillGoods> queryWrapper = getQueryWrapper(wmsDryInbillGoods);
        return wmsDryInbillGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增晾晒出入库单货物
     *
     * @param wmsDryInbillGoods 晾晒出入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsDryInbillGoods insertWmsDryInbillGoods(WmsDryInbillGoods wmsDryInbillGoods) {
        wmsDryInbillGoods.setId(IdUtil.simpleUUID());
        wmsDryInbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        wmsDryInbillGoodsMapper.insert(wmsDryInbillGoods);
        return wmsDryInbillGoods;
    }

    /**
     * 修改晾晒出入库单货物
     *
     * @param wmsDryInbillGoods 晾晒出入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsDryInbillGoods updateWmsDryInbillGoods(WmsDryInbillGoods wmsDryInbillGoods) {
        wmsDryInbillGoodsMapper.updateById(wmsDryInbillGoods);
        return wmsDryInbillGoods;
    }

    /**
     * 批量删除晾晒出入库单货物
     *
     * @param ids 需要删除的晾晒出入库单货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryInbillGoodsByIds(String[] ids) {
        List<WmsDryInbillGoods> wmsDryInbillGoodss = new ArrayList<>();
        for (String id : ids) {
            WmsDryInbillGoods wmsDryInbillGoods = new WmsDryInbillGoods();
            wmsDryInbillGoods.setId(id);
            wmsDryInbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsDryInbillGoodss.add(wmsDryInbillGoods);
        }
        List<WmsDryInbillGoods> list = wmsDryInbillGoodsMapper.selectBatchIds(Arrays.asList(ids));
        List<String> partsCodeList = list.stream().map(goods -> goods.getPartsCode()).collect(Collectors.toList());
        LambdaUpdateWrapper<DryOutbillGoods> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DryOutbillGoods::getLockStatus, LockEnum.NOTLOCK.getCode())
                .eq(DryOutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(DryOutbillGoods::getPartsCode, partsCodeList);
        wmsDryOutbillGoodsMapper.update(null, updateWrapper);
        return super.updateBatchById(wmsDryInbillGoodss) ? 1 : 0;
    }

    /**
     * 删除晾晒出入库单货物信息
     *
     * @param id 晾晒出入库单货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsDryInbillGoodsById(String id) {
        WmsDryInbillGoods wmsDryInbillGoods = new WmsDryInbillGoods();
        wmsDryInbillGoods.setId(id);
        wmsDryInbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsDryInbillGoodsMapper.updateById(wmsDryInbillGoods);
    }

    public QueryWrapper<WmsDryInbillGoods> getQueryWrapper(WmsDryInbillGoods wmsDryInbillGoods) {
        QueryWrapper<WmsDryInbillGoods> queryWrapper = new QueryWrapper<>();
        if (wmsDryInbillGoods != null) {
            wmsDryInbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsDryInbillGoods.getDelFlag());
            //晾晒出入库单号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getDryInbillCode())) {
                queryWrapper.eq("dry_inbill_code", wmsDryInbillGoods.getDryInbillCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getPartsCode())) {
                queryWrapper.like("parts_code", wmsDryInbillGoods.getPartsCode());
            }
            //货物编号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getGoodsCode())) {
                queryWrapper.eq("goods_code", wmsDryInbillGoods.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getGoodsName())) {
                queryWrapper.like("goods_name", wmsDryInbillGoods.getGoodsName());
            }

            //库区编号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getAreaCode())) {
                queryWrapper.eq("area_code", wmsDryInbillGoods.getAreaCode());
            }
            //库区名称
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getAreaName())) {
                queryWrapper.like("area_name", wmsDryInbillGoods.getAreaName());
            }
            //库位编号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getLocationCode())) {
                queryWrapper.eq("location_code", wmsDryInbillGoods.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getLocationName())) {
                queryWrapper.like("location_name", wmsDryInbillGoods.getLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getTrayCode())) {
                queryWrapper.eq("tray_code", wmsDryInbillGoods.getTrayCode());
            }
            //入库状态
            if (StrUtil.isNotEmpty(wmsDryInbillGoods.getDryInbillStatus())) {
                queryWrapper.eq("in_bill_status", wmsDryInbillGoods.getDryInbillStatus());
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wmsDryInbillGoodsList 模板数据
     * @param updateSupport         是否更新已经存在的数据
     * @param operName              操作人姓名
     * @return
     */
    public String importData(List<WmsDryInbillGoods> wmsDryInbillGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsDryInbillGoodsList) || wmsDryInbillGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsDryInbillGoods wmsDryInbillGoods : wmsDryInbillGoodsList) {
            if (null == wmsDryInbillGoods) {
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsDryInbillGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsDryInbillGoods);
                    wmsDryInbillGoods.setId(IdUtil.simpleUUID());
                    wmsDryInbillGoods.setCreateBy(operName);
                    wmsDryInbillGoods.setCreateTime(new Date());
                    wmsDryInbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsDryInbillGoodsMapper.insert(wmsDryInbillGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsDryInbillGoods);
                    //todo 验证
                    //int count = wmsDryInbillGoodsMapper.checkCode(wmsDryInbillGoods);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    wmsDryInbillGoods.setId(u.getId());
                    wmsDryInbillGoods.setUpdateBy(operName);
                    wmsDryInbillGoods.setUpdateTime(new Date());
                    wmsDryInbillGoodsMapper.updateById(wmsDryInbillGoods);
                    successNum++;
                    //}
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、第" + failureNum + "行数据导入失败，数据重复；");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、第" + failureNum + "行数据导入失败：";
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
     * 晾晒入库组盘
     *
     * @param dtoList
     */
    @Transactional(rollbackFor = Exception.class)
    public void groupDisk(List<DryInbillGroupDiskDto> dtoList) {
        //晾晒入库单号
        List<String> dryInbillCodeList = dtoList.stream().map(DryInbillGroupDiskDto::getDryInbillCode).distinct().collect(Collectors.toList());
        //托盘编号
        List<String> trayCodeList = dtoList.stream().map(DryInbillGroupDiskDto::getTrayCode).distinct().collect(Collectors.toList());
        //修改晾晒详情为已组盘
        LambdaUpdateWrapper<WmsDryInbillGoods> dryInbillGoodsUpdateWrapper = Wrappers.lambdaUpdate();
        dryInbillGoodsUpdateWrapper.set(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.TAKE.getCode())
                .in(WmsDryInbillGoods::getDryInbillCode, dryInbillCodeList)
                .in(WmsDryInbillGoods::getTrayCode, trayCodeList)
                .eq(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.GROUPIN.getCode());
        wmsDryInbillGoodsMapper.update(null, dryInbillGoodsUpdateWrapper);
        //根据晾晒入库详情状态修改晾晒入库抬头状态
        for (String dryInbillCode : dryInbillCodeList) {
            LambdaQueryWrapper<WmsDryInbillGoods> dryInbillGoodsQueryWrapper = Wrappers.lambdaQuery();
            dryInbillGoodsQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(WmsDryInbillGoods::getDryInbillCode, dryInbillCode)
                    .ne(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.TAKE.getCode());
            if (wmsDryInbillGoodsMapper.selectCount(dryInbillGoodsQueryWrapper) == 0) {//修改晾晒入库抬头状态为已组盘
                LambdaUpdateWrapper<WmsDryInbill> dryInbillUpdateWrapper = Wrappers.lambdaUpdate();
                dryInbillUpdateWrapper.set(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.TAKE.getCode())
                        .eq(WmsDryInbill::getDryInbillCode, dryInbillCode);
                wmsDryInbillMapper.update(null, dryInbillUpdateWrapper);
            }
        }
    }

    /**
     * 晾晒入库上架
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void putOn(DryInbillPutOnDto dto) {
        //晾晒入库单号
        String dryInbillCode = dto.getDryInbillCode();
        //查询状态为已组盘的该晾晒入库单号
        LambdaQueryWrapper<WmsDryInbill> dryInbillQueryWrapper = Wrappers.lambdaQuery();
        dryInbillQueryWrapper.select(WmsDryInbill::getId)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
//                .eq(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.TAKE.getCode())
                .eq(WmsDryInbill::getDryInbillCode, dryInbillCode);
        //晾晒入库单抬头
        WmsDryInbill wmsDryInbill = wmsDryInbillMapper.selectOne(dryInbillQueryWrapper);
        if (wmsDryInbill == null || StringUtils.isEmpty(wmsDryInbill.getId())) {
            throw new ServiceException("晾晒入库单数据异常");
        }
        //查询晾晒入库单下需要上架的托盘
//        QueryWrapper<WmsDryInbillGoods> wmsDryInbillGoodsquery = Wrappers.query();
//        wmsDryInbillGoodsquery.select("distinct tray_code")
//                .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
//                .eq("dry_inbill_code", dryInbillCode);
//        List<String> trayCodeList = wmsDryInbillGoodsMapper.selectObjs(wmsDryInbillGoodsquery).stream()
//                .map(String::valueOf).collect(Collectors.toList());
        List<String> trayCodeList = dto.getTrayCodeList();
        if(CollectionUtil.isEmpty(trayCodeList)){
            throw new ServiceException("托盘不能为空！");
        }
        //查询晾晒区库区编码
        LambdaQueryWrapper<Area> areaQueryWrapper = Wrappers.lambdaQuery();
        areaQueryWrapper.select(Area::getAreaCode)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaType, AreaTypeEnum.LSQ.getCode())
                .last("limit 1");
        Area area = areaMapper.selectOne(areaQueryWrapper);
        //开始库区编码(晾晒区)
        String startAreaCode = area.getAreaCode();
        //查询晾晒区agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, startAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        List<WmsWcsInfo> infoList = new ArrayList<>();//组装的数据
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，分配结束库区、结束库位
            //查询托盘在晾晒区的库位
            List<LocationMapVo> trayInfoList = trayMapper.getTrayInfoInCode(startAreaCode, trayCodeList);
            List<String> locationCodeList = trayInfoList.stream()
                    .map(trayInfo -> (String) trayInfo.getLocationCode()).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(locationCodeList)) {
                throw new ServiceException("托盘的起始位置获取失败，请确认是否人工搬运和AGV搬运混合使用");
            }
            //校验库位是否在细巷道里，是否可以回盘
            if (checkTunnel(locationCodeList)) {
                //锁定晾晒区库位
                LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
                locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                        .in(Location::getLocationCode, locationCodeList);
                locationService.getBaseMapper().update(null, locationUpdate);
                //分配存储区库位，组装wcs任务
                infoList = packageTask(trayInfoList);
            } else {
                throw new ServiceException("托盘被阻挡，不可上架");
            }
        } else {//agv禁用，在人工选择的结束库区中分配结束库位
            String endAreaCode = dto.getEndAreaCode();
            if (StringUtils.isEmpty(endAreaCode)) {
                throw new ServiceException("AGV禁用，请选择结束库区");
            }
            //给托盘分配结束库位，组装wcs任务
            infoList = packageTask(trayCodeList, endAreaCode);
        }
        //发送wcs任务
        infoList = infoList.stream().map(info -> {
            info.put(WmsWcsInfo.START_AREA_CODE, startAreaCode);
            info.put(WmsWcsInfo.DOC, dryInbillCode);
            Object chidList = info.get(WmsWcsInfo.CHILD_INFO_LIST);
            if(chidList != null){
                List<WmsWcsInfo> chids = (List<WmsWcsInfo>) chidList;
                for (WmsWcsInfo chid : chids) {
                    chid.put(WmsWcsInfo.DOC, dryInbillCode);
                }
            }
            return info;
        }).collect(Collectors.toList());
        //保存任务日志
        tasklogService.saveBatch(infoList);
        //把任务的数据给wcs
        List<WmsToWcsTaskReq> collect = infoList.stream().map(info2 -> {
            String jsonStr = JSONObject.toJSONString(info2);
            return JSONObject.parseObject(jsonStr, WmsToWcsTaskReq.class);
        }).collect(Collectors.toList());
        new Thread(() -> {
            wcsTaskApiService.agvLinkAgeStacker(collect);
        }).start();
        List<WmsDryInbillGoods> wmsDryInbillGoods = wmsDryInbillGoodsMapper.selectList(
                new QueryWrapper<WmsDryInbillGoods>()
                        .eq("dry_inbill_code", dryInbillCode)
                        .in("dry_inbill_status", DryInbillStatusEnum.GROUPIN.getCode())
                        .in("tray_code", trayCodeList)
        );
        String remarks = null;
        if (CollUtil.isNotEmpty(wmsDryInbillGoods)) {
            WmsDryInbill searchWmsDryInBill = wmsDryInbillMapper.selectOne(new QueryWrapper<WmsDryInbill>()
                    .eq("dry_inbill_code", dryInbillCode)
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
            );
            String trayCodeStr = "";
            if(StrUtil.isNotEmpty(searchWmsDryInBill.getRemark())){
                trayCodeStr = searchWmsDryInBill.getRemark().replaceAll("未组盘", "")+",";
            }
            remarks = trayCodeStr + CollUtil.join(trayCodeList, ",") + "未组盘";
        }

        //修改晾晒入库单抬头、详情为入库中
        LambdaUpdateWrapper<WmsDryInbillGoods> dryInbillGoodsUpdateWrapper = Wrappers.lambdaUpdate();
        dryInbillGoodsUpdateWrapper.set(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.ING.getCode())
                .eq(WmsDryInbillGoods::getDryInbillCode, dryInbillCode).in(WmsDryInbillGoods::getTrayCode, trayCodeList);
        wmsDryInbillGoodsMapper.update(null, dryInbillGoodsUpdateWrapper);
        LambdaUpdateWrapper<WmsDryInbill> dryInbillUpdateWrapper = Wrappers.lambdaUpdate();
        if(StrUtil.isNotEmpty(remarks)){
            dryInbillUpdateWrapper.set(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.ING.getCode())
                    .set(WmsDryInbill::getRemark,remarks)
                    .eq(WmsDryInbill::getDryInbillCode, dryInbillCode);
        }else{
            dryInbillUpdateWrapper.set(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.ING.getCode())
                    .eq(WmsDryInbill::getDryInbillCode, dryInbillCode);
        }

        wmsDryInbillMapper.update(null, dryInbillUpdateWrapper);

        //修改单据状态，为已入库
        if (wmsDryInbillGoodsMapper.selectCount(new QueryWrapper<WmsDryInbillGoods>().eq("dry_inbill_code", dryInbillCode)
                .eq("dry_inbill_status", DryInbillStatusEnum.TAKE.getCode())) == 0 &&
                wmsDryInbillGoodsMapper.selectCount(new QueryWrapper<WmsDryInbillGoods>().eq("dry_inbill_code", dryInbillCode)
                        .eq("dry_inbill_status", DryInbillStatusEnum.GROUPIN.getCode())) == 0) {
            LambdaUpdateWrapper<WmsDryInbill> wrapper = Wrappers.lambdaUpdate();
            wrapper.set(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.END.getCode())
                    .eq(WmsDryInbill::getDryInbillCode, dryInbillCode);
            wmsDryInbillMapper.update(null, wrapper);
            LambdaUpdateWrapper<WmsDryInbillGoods> wp = Wrappers.lambdaUpdate();
            wp.set(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.END.getCode())
                    .eq(WmsDryInbillGoods::getDryInbillCode, dryInbillCode).in(WmsDryInbillGoods::getTrayCode, trayCodeList);
            wmsDryInbillGoodsMapper.update(null, wp);
        }
    }


    /**
     * AGV启用，分配库位
     *
     * @param trayInfoList
     * @return
     */
    private List<WmsWcsInfo> packageTask(List<LocationMapVo> trayInfoList) {
        List<WmsWcsInfo> returnList = new ArrayList<>();
        //存储区的空库位 areaCode 库区编码，emptyNum 空库位数量
        List<AreaDto> areaMapList = areaMapper.selectAllAreaByType(AreaTypeEnum.CCQ.getCode(), null);
        //所有库区空库位总数
        Long emptyNum = areaMapList.stream().mapToLong(areaMap2 -> areaMap2.getEmptyNum()).sum();
        //需要的空库位总数
        int num = trayInfoList.size();
        if (emptyNum < num) {
            throw new ServiceException("空库位数量不足");
        }
        areaMapList = areaMapList.stream().filter(areaMap -> (Long) areaMap.getEmptyNum() > 0L).collect(Collectors.toList());
        //库区总数
        int size = areaMapList.size();
        if (size == 1) {
            areaMapList.get(0).setSureCount(Long.parseLong(num + ""));
            areaMapList.get(0).setTrayInfoList(trayInfoList);
        } else {
            //获取全局策略
            LambdaQueryWrapper<WmsTacticsConfig> tacticsQuery = Wrappers.lambdaQuery();
            tacticsQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .last(" limit 1");
            WmsTacticsConfig wmsTacticsConfig = wmsTacticsConfigMapper.selectOne(tacticsQuery);
            //策略
            String tactics = wmsTacticsConfig.getTactics();
            if (TacticsEnum.CONCENTRATE.getCode().equals(tactics)) {//集中堆放策略
                //剩余需分配空库位数
                Long needNum = Long.valueOf(num);
                //已分配托盘数
                int alreadyNum = 0;
                //集中堆放的库区编码顺序
                String tacticsContent = wmsTacticsConfig.getTacticsContent();
                String[] areaCodeArrays = tacticsContent.split(",");
                endLoop:
                for (String areaCode : areaCodeArrays) {
                    for (AreaDto areaMap : areaMapList) {
                        if (areaCode.equals(areaMap.getAreaCode())) {
                            //找到该库区后，获取该库区空库位数量
                            Long emptyCount = areaMap.getEmptyNum();
                            if (needNum - emptyCount >= 0) {
                                //该库区需分配空库位数为全部空库位
                                areaMap.setSureCount(emptyCount);
                                areaMap.setTrayInfoList(trayInfoList.subList(alreadyNum, (alreadyNum + emptyCount.intValue())));
                                needNum -= emptyCount;
                                alreadyNum += emptyCount;
                            } else {
                                //该库区需分配空库位数为needNum
                                areaMap.setSureCount(needNum);
                                areaMap.setTrayInfoList(trayInfoList.subList(alreadyNum, (alreadyNum + needNum.intValue())));
                                //跳出整个循环
                                break endLoop;
                            }
                        }
                    }
                }
            } else {//平均分配策略
                //根据托盘上货物类型分组
                List<String> goodsCodeList = trayInfoList.stream().map(info -> (String) info.getGoodsCode()).distinct().collect(Collectors.toList());
                for (String goodsCode : goodsCodeList) {
                    //该货物类型的托盘信息
                    List<LocationMapVo> list = trayInfoList
                            .stream()
                            .filter(info -> goodsCode.equals(info.getGoodsCode()))
                            .sorted(Comparator.comparing(LocationMapVo::getOrderNum).reversed())
                            .collect(Collectors.toList());
                    //获取各库区该货物编码所属托盘数量
                    List<AreaDto> goodsTrayCountByAreaType = trayMapper
                            .getGoodsTrayCountByAreaType(AreaTypeEnum.CCQ.getCode(), goodsCode);
                    for (AreaDto map : areaMapList) {
                        for (AreaDto m : goodsTrayCountByAreaType) {
                            if (m.getAreaCode().equals(map.getAreaCode())) {
                                Long count = Long.parseLong(m.getGoodsCount() + "");
                                map.setGoodsCount(count);
                            }
                        }
                    }
                    //分配空库位
                    for (int i = 0; i < list.size(); i++) {
                        //将库区list根据goodsCount正序排序
                        areaMapList = areaMapList.stream()
                                .sorted(Comparator.comparing(areaMap -> areaMap.getGoodsCount())).collect(Collectors.toList());
                        for (AreaDto areaMap : areaMapList) {
                            Long emptyCount = areaMap.getEmptyNum();//库区内的空库位数量
                            if (emptyCount > 0L) {//从该库区分配一个库位
                                areaMap.setEmptyNum(emptyCount - 1L);
                                if (areaMap.getSureCount() == 0L) {
                                    areaMap.setSureCount(1L);
                                    List<LocationMapVo> list2 = new ArrayList<>();
                                    list2.add(list.get(i));
                                    areaMap.setTrayInfoList(list2);
                                } else {
                                    areaMap.setSureCount(areaMap.getSureCount() + 1L);
                                    List<LocationMapVo> list2 = areaMap.getTrayInfoList();
                                    list2.add(list.get(i));
                                }
                                areaMap.setGoodsCount(areaMap.getGoodsCount() + 1L);
                                break;
                            } else {//去下一个库区分配空库位
                                continue;
                            }
                        }
                    }
                }
            }
        }

        List<String> hasParentLocationCodes = new ArrayList<>();
        areaMapList = areaMapList.stream().sorted(Comparator.comparing(areaMap -> areaMap.getAreaCode())).collect(Collectors.toList());
        //组装数据，回盘
        for (int i = 0; i < areaMapList.size(); i++) {
            AreaDto areaMap = areaMapList.get(i);
            if (areaMap != null && areaMap.getSureCount() == 0) {
                continue;
            }
            //结束库区
            String areaCode = areaMap.getAreaCode();
            //托盘信息
            List<LocationMapVo> list = areaMap.getTrayInfoList();

            //获取结束库位
            List<EmptyLocationBo> sureLocationList = locationService.getBaseMapper().getEmptyLocation(areaCode, Integer.valueOf(areaMap.getSureCount() + ""));

            List<String> sureLocations = sureLocationList.stream().map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
            //锁定结束库位
            locationService.getBaseMapper().lockLocation(sureLocations);
            for (int j = 0; j < list.size(); j++) {
                EmptyLocationBo locationMapVo = sureLocationList.get(j);
                if(locationMapVo.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())) {
                    WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, list.get(j).getTrayCode());
                    info.put(WmsWcsInfo.START_LOCATION_CODE, list.get(j).getLocationCode());
                    info.put(WmsWcsInfo.END_AREA_CODE, areaCode);
                    info.put(WmsWcsInfo.END_LOCATION_CODE, locationMapVo.getLocationCode());
                    info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                    returnList.add(info);
                    hasParentLocationCodes.add(locationMapVo.getLocationCode());
                }else{
                    Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("enable_status", EnableStatus.ENABLE.getCode())
                            .eq("location_code", locationMapVo.getLocationCode())
                    );
                    Location parentLocation = locationService.getParentLocation(childLocation, areaCode);

                    //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                    if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                        info.put(WmsWcsInfo.TRAY_CODE, list.get(j).getTrayCode());
                        info.put(WmsWcsInfo.START_LOCATION_CODE, list.get(j).getLocationCode());
                        info.put(WmsWcsInfo.END_AREA_CODE, areaCode);
                        info.put(WmsWcsInfo.END_LOCATION_CODE, locationMapVo.getLocationCode());
                        info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                        returnList.add(info);
                    }else{
                        String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(areaCode);

                        List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                        WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        info.put(WmsWcsInfo.START_LOCATION_CODE,  list.get(j).getLocationCode());
                        info.put(WmsWcsInfo.TRAY_CODE, list.get(j).getTrayCode());
                        info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                        info.put(WmsWcsInfo.END_AREA_CODE, areaCode);


                        //1.母库位移动至移库库位
                        WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                        childInfoList.add(childInfo);

                        //2.晾晒入库具体任务
                        WmsWcsInfo moveInfo = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                        moveInfo.put(WmsWcsInfo.TRAY_CODE, list.get(j).getTrayCode());
                        moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, list.get(j).getLocationCode());
                        moveInfo.put(WmsWcsInfo.END_AREA_CODE, areaCode);
                        moveInfo.put(WmsWcsInfo.END_LOCATION_CODE, locationMapVo.getLocationCode());
                        moveInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                        childInfoList.add(moveInfo);


                        //3.移库库位回至母库位
                        WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                        childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                        childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                        childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                        childInfoList.add(childInfo3);
                        info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                        returnList.add(info);
                    }
                }
            }
        }

        List<DryInBillAgvBo> dryInBillAgvBos = new ArrayList<>();
        for (WmsWcsInfo wmsWcsInfo : returnList) {
            DryInBillAgvBo dryInBillAgvBo = new DryInBillAgvBo();
            dryInBillAgvBo.setAreaCode(wmsWcsInfo.get(WmsWcsInfo.END_AREA_CODE)+"");
            dryInBillAgvBo.setLocationCode(wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE)+"");
            dryInBillAgvBos.add(dryInBillAgvBo);
        }

        //将细巷道托盘提到任务最前方
        List<WmsWcsInfo> returnList2 = new ArrayList<>();
        //在细巷道的库位
        List<String> locationList = new ArrayList<>();
        for (String location : DryLocationConstants.tunnelOnes) {
            for (int j = 0; j < returnList.size(); j++) {
                if (location.equals(returnList.get(j).get(WmsWcsInfo.START_LOCATION_CODE))) {
                    returnList2.add(returnList.get(j));
                    locationList.add(location);
                    break;
                }
            }
        }
        for (String location : DryLocationConstants.tunnelTwos) {
            for (int j = 0; j < returnList.size(); j++) {
                if (location.equals(returnList.get(j).get(WmsWcsInfo.START_LOCATION_CODE))) {
                    returnList2.add(returnList.get(j));
                    locationList.add(location);
                    break;
                }
            }
        }
        //不在细巷道的其他库位
        List<WmsWcsInfo> collect = returnList.stream()
                .filter(info -> !locationList.contains(info.get(WmsWcsInfo.START_LOCATION_CODE))).collect(Collectors.toList());
        returnList2.addAll(collect);

        List<String> userLocationCode = new ArrayList<>();
        for (WmsWcsInfo wmsWcsInfo : returnList2) {
            for (DryInBillAgvBo dryInBillAgvBo : dryInBillAgvBos) {
                if(dryInBillAgvBo.getAreaCode().equals(wmsWcsInfo.get(WmsWcsInfo.END_AREA_CODE))){
                    if(!userLocationCode.contains(dryInBillAgvBo.getLocationCode())){
                        wmsWcsInfo.put(WmsWcsInfo.END_LOCATION_CODE,dryInBillAgvBo.getLocationCode());
                        userLocationCode.add(dryInBillAgvBo.getLocationCode());
                        break;
                    }
                }
            }
        }
        return returnList2;
    }


    /**
     * agv禁用，分配库位
     *
     * @param trayCodeList
     * @param endAreaCode
     * @return
     */
    private List<WmsWcsInfo> packageTask(List<String> trayCodeList, String endAreaCode) {
        List<WmsWcsInfo> returnList = new ArrayList<>();
        List<String> hasParentLocationCodes = new ArrayList<>();
        //查询end库区下空库位
        List<EmptyLocationBo> emptyLocation = locationService.getBaseMapper().getEmptyLocation(endAreaCode, trayCodeList.size());
        if (emptyLocation.size() != trayCodeList.size()) {
            throw new ServiceException("库区剩余空库位不足");
        }
        List<String> emptyLocations = emptyLocation.stream().map(EmptyLocationBo::getLocationCode).collect(Collectors.toList());
        //锁定存储区空库位
        LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
        locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                .in(Location::getLocationCode, emptyLocations);
        locationService.getBaseMapper().update(null, locationUpdate);
        for (int i = 0; i < emptyLocation.size(); i++) {
            EmptyLocationBo emptyLocationBo = emptyLocation.get(i);
            if(emptyLocationBo.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())){
                WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                info.put(WmsWcsInfo.TRAY_CODE, trayCodeList.get(i));
                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                info.put(WmsWcsInfo.END_LOCATION_CODE,emptyLocationBo.getLocationCode());
                info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                returnList.add(info);
                hasParentLocationCodes.add(emptyLocationBo.getLocationCode());
            }else{
                Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                        .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                        .eq("enable_status", EnableStatus.ENABLE.getCode())
                        .eq("location_code", emptyLocationBo.getLocationCode())
                );
                Location parentLocation = locationService.getParentLocation(childLocation, endAreaCode);

                //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                    WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, trayCodeList.get(i));
                    info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                    info.put(WmsWcsInfo.END_LOCATION_CODE, emptyLocationBo.getLocationCode());
                    info.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                    returnList.add(info);
                }else{
                    String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(endAreaCode);

                    List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                    WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);

                    //1.母库位移动至移库库位
                    WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                    childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                    childInfo.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfoList.add(childInfo);

                    //移库的时候，先将母库位进行锁定（防止连续上架，第二次任务会上架至母库位（有托盘））
                    LambdaUpdateWrapper<Location> locationUpdate1 = Wrappers.lambdaUpdate();
                    locationUpdate1.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                            .in(Location::getLocationCode, parentLocation.getLocationCode());
                    locationService.getBaseMapper().update(null, locationUpdate1);

                    //2.晾晒入库具体任务
                    WmsWcsInfo moveInfo = new WmsWcsInfo(WmsWcsTypeEnum.PUTTRAY.getCode(), WmsWcsTaskTypeEnum.DRY_STORAGE.getCode());
                    moveInfo.put(WmsWcsInfo.TRAY_CODE, trayCodeList.get(i));
                    moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                    moveInfo.put(WmsWcsInfo.END_LOCATION_CODE, emptyLocationBo.getLocationCode());
                    moveInfo.put(WmsWcsInfo.AREATYPE, AreaTypeEnum.LSQ.getCode());
                    childInfoList.add(moveInfo);

                    //3.移库库位回至母库位
                    WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                    childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                    childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                    childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                    childInfo3.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.END_AREA_CODE, parentLocation.getAreaId());
                    childInfo3.put(WmsWcsInfo.MOVE_LAST, "true");

                    childInfoList.add(childInfo3);
                    info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);

                    returnList.add(info);
                }
            }
        }
        return returnList;
    }


    private boolean checkTunnel(List<String> locationCodeList) {

        boolean flag = true;
        //获取库位顺序
        LambdaQueryWrapper<Location> locationWrapper = Wrappers.lambdaQuery();
        locationWrapper.select(Location::getLocationCode);
        locationWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Location::getAreaId, "LSQ01")
                .in(Location::getLocationCode, locationCodeList)
                .orderByDesc(Location::getOrderNum);
        List<Location> locations = locationService.getBaseMapper().selectList(locationWrapper);
        List<String> locationCodes = locations.stream().map(location -> (String) location.getLocationCode()).collect(Collectors.toList());

        //所有需要查询有无货的下标索引
        Set<String> codeSet = new HashSet<>();
        for (String s : locationCodes) {
            // 第一排
            Optional<String> ones = Arrays.stream(DryLocationConstants.tunnelOnes).filter(str -> str.equals(s)).findFirst();
            if (ones.isPresent()) {
                Integer index = getIndex(s, DryLocationConstants.tunnelOnes);
                if (index == null) {
                    flag = false;
                    break;
                }
                if (index != 0) {
                    for (int i = 0; i < DryLocationConstants.tunnelOnes.length; i++) {
                        if (i < index) {
                            codeSet.add(DryLocationConstants.tunnelOnes[i]);
                        } else {
                            break;
                        }
                    }
                }
            }
            // 第二排
            Optional<String> twos = Arrays.stream(DryLocationConstants.tunnelTwos).filter(str -> str.equals(s)).findFirst();
            if (twos.isPresent()) {
                Integer index = getIndex(s, DryLocationConstants.tunnelTwos);
                if (index == null) {
                    flag = false;
                    break;
                }
                if (index != 0) {
                    for (int i = 0; i < DryLocationConstants.tunnelTwos.length; i++) {
                        if (i < index) {
                            codeSet.add(DryLocationConstants.tunnelTwos[i]);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        if(CollUtil.isNotEmpty(codeSet)){
            for (String locationCode : locationCodes) {
                codeSet.remove(locationCode);
            }
        }
        if(CollUtil.isNotEmpty(codeSet)){
            //校验前一个库位是否为空库位
            LambdaQueryWrapper<Location> locationQueryWrapper = Wrappers.lambdaQuery();
            locationQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .eq(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .isNotNull(Location::getTrayCode)
                    .in(Location::getLocationCode, codeSet);
            if (locationService.getBaseMapper().selectCount(locationQueryWrapper) > 0) {
                flag = false;
            }
        }
        return  flag;
    }

    //获取理货区库位下标
    private Integer getIndex(String str,String[] tunnel){
        Integer index = null;
        for (int i = 0; i < tunnel.length; i++) {
            if(str.equals(tunnel[i])){
                index = i;
                break;
            }
        }
        return index;
    }


    /**
     * 晾晒入库结束
     *
     * @param wmsWcsInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void dryingInEnd(WmsWcsInfo wmsWcsInfo) {
        String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
        String doc = (String) wmsWcsInfo.get(WmsWcsInfo.DOC);//晾晒单号
        //修改晾晒详情任务已完成
        LambdaUpdateWrapper<WmsDryInbillGoods> dryInbillGoodsUpdateWrapper = Wrappers.lambdaUpdate();
        dryInbillGoodsUpdateWrapper.set(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.END.getCode())
                .set(WmsDryInbillGoods::getLocationCode, wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE))
                .set(WmsDryInbillGoods::getDryInbillTime, new Date())
                .eq(WmsDryInbillGoods::getTrayCode, trayCode)
                .eq(WmsDryInbillGoods::getDryInbillCode, doc);
        wmsDryInbillGoodsMapper.update(null, dryInbillGoodsUpdateWrapper);
        //根据详情完成情况修改抬头晾晒状态
        LambdaQueryWrapper<WmsDryInbillGoods> dryInbillGoodsQueryWrapper = Wrappers.lambdaQuery();
        dryInbillGoodsQueryWrapper.eq(WmsDryInbillGoods::getDryInbillCode, doc)
                .ne(WmsDryInbillGoods::getDryInbillStatus, DryInbillStatusEnum.END.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        if (wmsDryInbillGoodsMapper.selectCount(dryInbillGoodsQueryWrapper) == 0) {//修改晾晒抬头为已完成
            LambdaUpdateWrapper<WmsDryInbill> dryInbillUpdateWrapper = Wrappers.lambdaUpdate();
            dryInbillUpdateWrapper.set(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.END.getCode())
                    .set(WmsDryInbill::getDryInbillTime, new Date())
                    .eq(WmsDryInbill::getDryInbillCode, doc);
            wmsDryInbillMapper.update(null, dryInbillUpdateWrapper);
        }
    }

    /**
     * 晾晒入库上架页面查看详情
     *
     * @param dryInbillCode
     * @return
     */
    public List<DryInBillTrayVo> selectDryInbillGoods(String dryInbillCode) {
        List<DryInBillTrayVo> voList = new ArrayList<>();
        LambdaQueryWrapper<WmsDryInbillGoods> dryInbillQueryWrapper = Wrappers.lambdaQuery();
        dryInbillQueryWrapper.select(WmsDryInbillGoods::getTrayCode, WmsDryInbillGoods::getPartsCode, WmsDryInbillGoods::getDryInbillStatus)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WmsDryInbillGoods::getDryInbillCode, dryInbillCode);
        List<WmsDryInbillGoods> wmsDryInbillGoodList = wmsDryInbillGoodsMapper.selectList(dryInbillQueryWrapper);
        //晾晒入库单下托盘
        List<String> trayCodeList = wmsDryInbillGoodList.stream().map(WmsDryInbillGoods::getTrayCode).distinct().collect(Collectors.toList());

        List<String> actualCodeList = new ArrayList<>();
        if (CollUtil.isNotEmpty(trayCodeList)) {
            List<String> areaIds = areaService.selectAreaCodeByType(AreaTypeEnum.CCQ.getCode());
            List<String> hasPutOnTrayCodeList = new ArrayList<>();
            List<Location> list = locationService.getBaseMapper().selectList(
                    new QueryWrapper<Location>()
                            .in("tray_code", trayCodeList)
                            .in("area_id", areaIds)
            );
            if (CollUtil.isNotEmpty(list)) {
                hasPutOnTrayCodeList = list.stream().map(Location::getTrayCode).collect(Collectors.toList());
            }
            for (String item : trayCodeList) {
                if (!hasPutOnTrayCodeList.contains(item)) {
                    actualCodeList.add(item);
                }
            }
        }
        for (String trayCode : actualCodeList) {
            DryInBillTrayVo trayVo = new DryInBillTrayVo();
            trayVo.setTrayCode(trayCode);
            //托盘上的机件号列表
            List<String> collect = wmsDryInbillGoodList.stream().filter(inbill -> trayCode.equals(inbill.getTrayCode())).map(WmsDryInbillGoods::getPartsCode).collect(Collectors.toList());
            long hasDryTakeCount = wmsDryInbillGoodList.stream().filter(inbill -> trayCode.equals(inbill.getTrayCode()) && inbill.getDryInbillStatus().equals(DryInbillStatusEnum.TAKE.getCode())).count();
            trayVo.setTakeStatus(collect.size() == hasDryTakeCount);
            trayVo.setPartsCodeList(collect);
            voList.add(trayVo);
        }
        return voList;
    }

}
