package com.ruoyi.wms.outbound.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.api.service.WcsTaskApiService;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.domain.WcsOperateTask;
import com.ruoyi.wcs.enums.wcs.WcsTaskDeviceTypeEnum;
import com.ruoyi.wcs.mapper.WcsDeviceBaseInfoMapper;
import com.ruoyi.wcs.mapper.WcsOperateTaskMapper;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.domain.Warehouse;
import com.ruoyi.wms.basics.mapper.AreaMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.mapper.WarehouseMapper;
import com.ruoyi.wms.basics.service.LocationService;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.dto.OutbillGoodsDto;
import com.ruoyi.wms.outbound.mapper.OutBillMapper;
import com.ruoyi.wms.outbound.mapper.OutbillGoodsMapper;
import com.ruoyi.wms.outbound.vo.OutbillVo;
import com.ruoyi.wms.outbound.vo.PartsCodeVo;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.domain.WmsAccount;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.service.WmsAccountService;
import com.ruoyi.wms.utils.constant.LhqLocationConstants;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出库单货物Service接口
 *
 * @author ruoyi
 * @date 2023-02-07
 */
@Slf4j
@Service
public class OutbillGoodsService extends ServiceImpl<OutbillGoodsMapper, OutbillGoods> {

    @Autowired
    protected Validator validator;

    @Autowired
    private OutbillGoodsMapper outbillGoodsMapper;

    @Autowired
    private OutBillMapper outBillMapper;

    @Autowired
    private TblstockMapper tblstockMapper;

    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;

    @Autowired
    private TrayService trayService;

    @Autowired
    private TrayMapper trayMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private WaittaskService waittaskService;

    @Autowired
    private WmsAccountService wmsAccountService;

    @Autowired
    private WcsTaskApiService wcsTaskApiService;

    @Autowired(required = false)
    private WcsDeviceBaseInfoMapper wcsDeviceBaseInfoMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private WcsOperateTaskMapper wcsOperateTaskMapper;

    @Autowired
    private WmsWarehouseCheckDetailService checkDetailService;

    /**
     * 查询出库单货物
     *
     * @param id 出库单货物主键
     * @return 出库单货物
     */
    public OutbillGoods selectOutbillGoodsById(String id) {
        QueryWrapper<OutbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return outbillGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询出库单货物
     *
     * @param ids 出库单货物 IDs
     * @return 出库单货物
     */
    public List<OutbillGoods> selectOutbillGoodsByIds(String[] ids) {
        QueryWrapper<OutbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return outbillGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询出库单货物列表
     *
     * @param outbillGoods 出库单货物
     * @return 出库单货物集合
     */
    public List<OutbillGoods> selectOutbillGoodsList(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> queryWrapper = getQueryWrapper(outbillGoods);
        return outbillGoodsMapper.select(queryWrapper);
    }

    /**
     * 分组查询出库单货物列表
     *
     * @param outbillGoods 出库单货物
     * @return 出库单货物集合
     */
    public List<OutbillGoods> selectList(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> queryWrapper = getQueryWrapperByGroup(outbillGoods);
        return outbillGoodsMapper.selectListByGroup(queryWrapper);
    }

    /**
     * 分组查询出库单货物列表
     *
     * @param outbillGoods 出库单货物
     * @return 出库单货物集合
     */
    public List<OutbillGoods> listGroup(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> queryWrapper = getQueryWrapper(outbillGoods);
        return outbillGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增出库单货物
     *
     * @param outbillGoods 出库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OutbillGoods insertOutbillGoods(OutbillGoods outbillGoods) {
        outbillGoods.setId(IdUtil.simpleUUID());
        outbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        outbillGoods.setOutBillStatus(OutBillGoodsEnum.WAIT.getCode());
        outbillGoodsMapper.insert(outbillGoods);
        return outbillGoods;
    }
    /**
     * 批量新增出库单货物
     *
     * @param outbillGoodsList 出库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<OutbillGoods> outbillGoodsList) {
        List<OutbillGoods> collect = outbillGoodsList.stream().map(outbillGoods -> {
            outbillGoods.setId(IdUtil.simpleUUID());
            outbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            outbillGoods.setOutBillStatus(OutBillGoodsEnum.WAIT.getCode());
            return outbillGoods;
        }).collect(Collectors.toList());
        return this.saveBatch(collect,collect.size());
    }

    /**
     * 修改出库单货物
     *
     * @param outbillGoods 出库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OutbillGoods updateOutbillGoods(OutbillGoods outbillGoods) {
        outbillGoodsMapper.updateById(outbillGoods);
        return outbillGoods;
    }

    /**
     * 批量删除出库单货物
     *
     * @param ids 需要删除的出库单货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOutbillGoodsByIds(String[] ids) {
        List<OutbillGoods> outbillGoodss = new ArrayList<>();
        for (String id : ids) {
            OutbillGoods outbillGoods = new OutbillGoods();
            outbillGoods.setId(id);
            outbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            outbillGoodss.add(outbillGoods);
        }
        List<OutbillGoods> outbillGoodsList = outbillGoodsMapper.selectBatchIds(Arrays.asList(ids));
        List<String> onlyCodes = outbillGoodsList.stream().map(outbillGoods -> outbillGoods.getOnlyCode()).collect(Collectors.toList());
        //解锁库存总览数据
        LambdaUpdateWrapper<Tblstock> tblstockLambdaUpdate = new LambdaUpdateWrapper<>();
        tblstockLambdaUpdate.set(Tblstock::getLockStatus, LockEnum.NOTLOCK.getCode())
                .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .in(Tblstock::getOnlyCode,onlyCodes);
        tblstockMapper.update(null,tblstockLambdaUpdate);
        return super.removeByIds(outbillGoodss) ? 1 : 0;
    }

    /**
     * 删除出库单货物信息
     *
     * @param id 出库单货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOutbillGoodsById(String id) {
        OutbillGoods outbillGoods = new OutbillGoods();
        outbillGoods.setId(id);
        outbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return outbillGoodsMapper.updateById(outbillGoods);
    }

    public QueryWrapper<OutbillGoods> getQueryWrapper(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> queryWrapper = new QueryWrapper<>();
        if (outbillGoods != null) {
            //出库单号
            if (StrUtil.isNotEmpty(outbillGoods.getOutBillCode())) {
                queryWrapper.eq("out_bill_code", outbillGoods.getOutBillCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(outbillGoods.getOnlyCode())) {
                queryWrapper.eq("only_code", outbillGoods.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(outbillGoods.getPartsCode())) {
                queryWrapper.eq("parts_code", outbillGoods.getPartsCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(outbillGoods.getGoodsCode())) {
                queryWrapper.like("goods_code", outbillGoods.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(outbillGoods.getGoodsName())) {
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("goods_name", outbillGoods.getGoodsName()).or();
                    QueryWrapper.like("only_code", outbillGoods.getGoodsName()).or();
                    QueryWrapper.like("model", outbillGoods.getGoodsName());
                });
            }
            //规格型号
            if (StrUtil.isNotEmpty(outbillGoods.getModel())) {
                queryWrapper.eq("model", outbillGoods.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(outbillGoods.getMeasureUnit())) {
                queryWrapper.eq("measure_unit", outbillGoods.getMeasureUnit());
            }
            //批次
            if (StrUtil.isNotEmpty(outbillGoods.getCharg())) {
                queryWrapper.eq("charg", outbillGoods.getCharg());
            }
            //供应商编码
            if (StrUtil.isNotEmpty(outbillGoods.getSupplierCode())) {
                queryWrapper.eq("supplier_code", outbillGoods.getSupplierCode());
            }
            //供应商名称
            if (StrUtil.isNotEmpty(outbillGoods.getSupplierName())) {
                queryWrapper.like("supplier_name", outbillGoods.getSupplierName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(outbillGoods.getTrayCode())) {
                queryWrapper.eq("tray_code", outbillGoods.getTrayCode());
            }
            //出库数量
            if (outbillGoods.getOutBillNum() != null) {
                queryWrapper.eq("out_bill_num", outbillGoods.getOutBillNum());
            }
            //出库状态;(0-待拣货 1-已取出 2-已拣货)
            if (StrUtil.isNotEmpty(outbillGoods.getOutBillStatus())) {
                queryWrapper.eq("out_bill_status", outbillGoods.getOutBillStatus());
            }
            //出库时间
            if (outbillGoods.getOutBillTime() != null) {
                queryWrapper.eq("out_bill_time", outbillGoods.getOutBillTime());
            }
            //前端临时删除id
            if (CollectionUtil.isNotEmpty(outbillGoods.getIds())) {
                queryWrapper.notIn("id", outbillGoods.getIds());
            }

        }
        return queryWrapper;
    }

    public QueryWrapper<OutbillGoods> getQueryWrapperByGroup(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> queryWrapper = new QueryWrapper<>();
        if (outbillGoods != null) {


            //出库单号
            if (StrUtil.isNotEmpty(outbillGoods.getOutBillCode())) {
                queryWrapper.eq("out_bill_code", outbillGoods.getOutBillCode());
            }

            //货物编码
            if (StrUtil.isNotEmpty(outbillGoods.getGoodsCode())) {
//                queryWrapper.like("goods_code", outbillGoods.getGoodsCode());
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("goods_code", outbillGoods.getGoodsCode()).or();
                    QueryWrapper.like("goods_name", outbillGoods.getGoodsCode()).or();
                    QueryWrapper.like("model", outbillGoods.getGoodsCode()).or();
                    QueryWrapper.like("charg", outbillGoods.getGoodsCode()).or();
                    QueryWrapper.like("tray_code", outbillGoods.getGoodsCode());
                });
            }

        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param outbillGoodsList 模板数据
     * @param updateSupport    是否更新已经存在的数据
     * @param operName         操作人姓名
     * @return
     */
    public String importData(List<OutbillGoods> outbillGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(outbillGoodsList) || outbillGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (OutbillGoods outbillGoods : outbillGoodsList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                OutbillGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, outbillGoods);
                    outbillGoods.setId(IdUtil.simpleUUID());
                    outbillGoods.setCreateBy(operName);
                    outbillGoods.setCreateTime(new Date());
                    outbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    outbillGoodsMapper.insert(outbillGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, outbillGoods);
                    //todo 验证
                    //int count = outbillGoodsMapper.checkCode(outbillGoods);
                    //if(count>0){//判断是否重复
                    //failureNum++;
                    //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                    outbillGoods.setId(u.getId());
                    outbillGoods.setUpdateBy(operName);
                    outbillGoods.setUpdateTime(new Date());
                    outbillGoodsMapper.updateById(outbillGoods);
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
     * 查询出库详情-PDA
     *
     * @param outbillGoods 出库单货物
     * @return
     */
    public List<OutbillGoods> selectOutbillDetail(OutbillGoods outbillGoods) {
        QueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.query();
        outbillGoodsQuery.eq("a.del_flag", DelFlagEnum.DEL_NO.getCode())
                .eq("a.out_bill_code", outbillGoods.getOutBillCode())
                .eq("a.out_bill_status", outbillGoods.getOutBillStatus())
                .orderByAsc("b.location_type");
        List<OutbillGoods> outbillGoods1 = outbillGoodsMapper.selectOutbillDetail(outbillGoodsQuery);
        //排序
        List<OutbillGoods> ccq01 = outbillGoods1.stream().filter(item -> "CCQ01".equals(item.getAreaCode())).collect(Collectors.toList());
        List<OutbillGoods> ccq02 = outbillGoods1.stream().filter(item -> "CCQ02".equals(item.getAreaCode())).collect(Collectors.toList());
        List<OutbillGoods> ccq03 = outbillGoods1.stream().filter(item -> "CCQ03".equals(item.getAreaCode())).collect(Collectors.toList());
        int size1 = ccq01.size();
        int size2 = ccq02.size();
        int size3 = ccq03.size();
        int maxSize = Math.max(size1, Math.max(size2, size3));
        List<OutbillGoods> sortedList = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {
            if (i < size1) {
                sortedList.add(ccq01.get(i));
            }
            if (i < size2) {
                sortedList.add(ccq02.get(i));
            }
            if (i < size3) {
                sortedList.add(ccq03.get(i));
            }
        }
        return sortedList;
    }

    /**
     * 根据托盘号获取货物清单
     *
     * @param
     */
    private OutbillVo getGoodsList(String trayCode, List<OutbillGoods> outbillGoodsList) {
        OutbillVo vo = new OutbillVo();
        vo.setTrayCode(trayCode);
        vo.setOutBillCode(outbillGoodsList.get(0).getOutBillCode());
        //托盘下的货物信息
        List<OutbillGoods> collect = outbillGoodsList.stream()
                .filter(outbillGoods -> trayCode.equals(outbillGoods.getTrayCode())).collect(Collectors.toList());
        //库存中托盘上的机件号
        List<String> partsCodeList = tblstockMapper.selectPartsCodeByTrayCode(trayCode);
        //托盘上的机件号
        List<String> trayPartsCodeList = collect.stream().map(OutbillGoods::getPartsCode).collect(Collectors.toList());
        if (partsCodeList.size() == trayPartsCodeList.size()) {
            vo.setStatus("全取");
            vo.setPartsCodeList(partsCodeList.stream().map(code -> {
                PartsCodeVo partsCodeVo = new PartsCodeVo();
                partsCodeVo.setPartsCode(code);
                partsCodeVo.setStatus(true);
                return partsCodeVo;
            }).collect(Collectors.toList()));
        } else {
            vo.setStatus("部分取");
            vo.setPartsCodeList(partsCodeList.stream().map(code -> {
                PartsCodeVo partsCodeVo = new PartsCodeVo();
                partsCodeVo.setPartsCode(code);
                partsCodeVo.setStatus(trayPartsCodeList.contains(code));
                return partsCodeVo;
            }).collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 出库取盘
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult takeTray(OutbillGoodsDto map) {
        if(null==map){
          throw new ServiceException("出库取盘参数不可为null");
        }
        //结束库区编码（理货区）
        String endAreaCode = map.getEndAreaCode();
        //出库单号
        String outBillCode = map.getOutBillCode();
        //托盘编号
        List<String> trayCodeList = map.getTrayCodeList();
        if (StringUtils.isEmpty(endAreaCode)||StringUtils.isEmpty(outBillCode)||StringUtils.isEmpty(trayCodeList)){
            throw new ServiceException("参数缺失");
        }
        List<Integer> enableStacker = wcsTaskApiService.getEnableStacker();
        if(CollUtil.isEmpty(enableStacker)){
            throw new ServiceException("堆垛机目前都处于手动模式，取盘失败");
        }
        if (checkDetailService.haveChecking()) {
            throw new ServiceException("盘点任务进行中，取盘失败");
        }
        LambdaQueryWrapper<WcsOperateTask> operateTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskType,WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode());
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getOperateType,WmsWcsTypeEnum.TAKETRAY.getCode());
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getTaskDeviceType,'2');
        operateTaskLambdaQueryWrapper.in(WcsOperateTask::getTaskStatus, "0",'1');
        operateTaskLambdaQueryWrapper.eq(WcsOperateTask::getDelFlag,  EnableStatus.ENABLE.getCode());
        if(wcsOperateTaskMapper.selectCount(operateTaskLambdaQueryWrapper) > 0){
            throw new ServiceException("堆垛机出库取盘操作有未执行完成的任务，请稍后再试");
        }
        //查询托盘是否可取用
        List<LocationMapVo> trayList = trayMapper.getTrayInfoInTrayCode(AreaTypeEnum.CCQ.getCode(), trayCodeList);
//        if(stackerOnline.size()>0){
//            List<String> areaCode = stackerOnline.stream().map(LocationMapVo::getAreaCode).distinct().collect(Collectors.toList());
//            throw new ServiceException(areaCode+"堆垛机目前处于手动模式，取盘失败");
//        }
        if (trayCodeList.size() > trayList.size()) {//有不可取用的托盘
            List<String> noTrayCodeList = trayList.stream().map(tray -> (String) tray.getTrayCode()).collect(Collectors.toList());
            List<String> collect = trayCodeList.stream().filter(trayCode -> !noTrayCodeList.contains(trayCode)).collect(Collectors.toList());
            throw new ServiceException("库位禁用/托盘禁用/托盘不在存储区,以下托盘不可取：" + collect);
        }
        List<String> areaIds = wcsTaskApiService.getStoreAreaIdByEnableStackerId(enableStacker);
        trayList = trayList.parallelStream().filter(item -> areaIds.contains(item.getAreaCode())).collect(Collectors.toList());
        trayCodeList = trayList.stream().map(LocationMapVo::getTrayCode).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(trayList)){
            throw new ServiceException("当前任务不可提交，堆垛机出现故障！");
        }

        //获取agv是否启用
        LambdaQueryWrapper<WcsDeviceBaseInfo> wcsDeviceBaseInfoQuery = Wrappers.lambdaQuery();
        wcsDeviceBaseInfoQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(WcsDeviceBaseInfo::getWarehouseAreaCode, endAreaCode)
                .eq(WcsDeviceBaseInfo::getDeviceType, WcsTaskDeviceTypeEnum.AVG.getCode())
                .eq(WcsDeviceBaseInfo::getEnableStatus, EnableStatus.ENABLE.getCode());
        if (wcsDeviceBaseInfoMapper.selectCount(wcsDeviceBaseInfoQuery) > 0) {//agv启用，锁定end库区库位
            //校验理货区库位数据
            LambdaQueryWrapper<Location> noTrayLocation = Wrappers.lambdaQuery();
            noTrayLocation.eq(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                    .eq(Location::getAreaId,endAreaCode)
                    .isNull(Location::getTrayCode);
            Long emptySize = locationMapper.selectCount(noTrayLocation);
            if(trayCodeList.size() > emptySize){
                throw new ServiceException("理货区空闲数量不足,剩余空库位"+emptySize);
            }
            List<LocationMapVo> ccq01 = trayList.stream().filter(item -> "CCQ01".equals(item.getAreaCode())).collect(Collectors.toList());
            if(ccq01.size()>0){
                //校验存储区1 理货区库位数据
                String stagingOnesStr = String.join(",", LhqLocationConstants.stagingOnes);
                LambdaQueryWrapper<Location> noCcq01TrayLocation = Wrappers.lambdaQuery();
                noCcq01TrayLocation.eq(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                        .eq(Location::getAreaId,endAreaCode)
                        .in(Location::getLocationCode,stagingOnesStr.split(","))
                        .isNull(Location::getTrayCode);
                Long Ccq01EmptyNum = locationMapper.selectCount(noCcq01TrayLocation);
                if(Ccq01EmptyNum < ccq01.size()){
                    throw new ServiceException("理货区库位不足,141号AGV理货区目前空余库位数量为:"+ Ccq01EmptyNum);
                }
            }
            List<LocationMapVo> ccq023 = trayList.stream().filter(item -> !"CCQ01".equals(item.getAreaCode())).collect(Collectors.toList());
            if(ccq023.size()>0){
                //校验存储区2、3 理货区库位数据
                String stagingTwosStr = String.join(",", LhqLocationConstants.stagingTwos);
                LambdaQueryWrapper<Location> noCcq023TrayLocation = Wrappers.lambdaQuery();
                noCcq023TrayLocation.eq(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                        .eq(Location::getAreaId,endAreaCode)
                        .in(Location::getLocationCode, stagingTwosStr.split(","))
                        .isNull(Location::getTrayCode);
                Long Ccq023EmptyNum = locationMapper.selectCount(noCcq023TrayLocation);
                if(Ccq023EmptyNum < ccq023.size()){
                    throw new ServiceException("理货区库位不足,140号AGV理货区目前空余库位数量为" + Ccq023EmptyNum);
                }
            }
        }
        //锁定库位
        List<String> locationCodeList = trayList.stream().map(tray -> (String) tray.getLocationCode()).collect(Collectors.toList());
        LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
        locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                .in(Location::getLocationCode, locationCodeList);
        locationService.getBaseMapper().update(null, locationUpdate);
        //库区编码列表
        List<String> areaCodeList = trayList.stream()
                .map(tray -> (String) tray.getAreaCode()).distinct().collect(Collectors.toList());
        //库区个数
        int size = areaCodeList.size();
        //交叠组装数据，取盘
        List<WmsWcsInfo> infoList = new ArrayList<>();
        Map<Integer, WmsWcsInfo> infoMap = new HashMap<>();
        List<String> hasParentLocationCodes = new ArrayList<>();
        for (int i = 0; i < areaCodeList.size(); i++) {
            //库区编码
            String areaCode = areaCodeList.get(i);
            //该库区下的托盘
            List<LocationMapVo> areaTrayList = trayList.stream()
                    .filter(tray -> areaCode.equals((String) tray.getAreaCode()))
                    .sorted(Comparator.comparing(LocationMapVo::getOrderNum))
                    .collect(Collectors.toList());
            //交叠组装数据
            for (int j = 0; j < areaTrayList.size(); j++) {
                LocationMapVo tray = areaTrayList.get(j);
                if(tray.getLocationType().equals(LocationTypeEnum.PARENT_LOCATION_TYPE.getCode())) {
                    WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode());
                    info.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                    info.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                    info.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                    info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                    info.put(WmsWcsInfo.DOC, outBillCode);
                    infoMap.put(i + size * j, info);
                    hasParentLocationCodes.add(tray.getLocationCode()+"");
                }else{
                    String trayAreaCode = tray.getAreaCode();
                    Integer locationOrderNum = tray.getOrderNum();
                    Location childLocation = locationService.getBaseMapper().selectOne(new QueryWrapper<Location>()
                            .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                            .eq("enable_status", EnableStatus.ENABLE.getCode())
                            .eq("location_code", tray.getLocationCode())
                    );
                    //根据子库位和区域id查询对应的父库位信息
                    Location parentLocation = locationService.getParentLocation(childLocation, trayAreaCode);
                    String moveLocationCode = areaMapper.selectMoveLocationCodeByAreaCode(trayAreaCode);
                    if (StringUtils.isNotEmpty(moveLocationCode)) {
                        //如何hasParentLocationCodes包含母库位，则表示子母库位在本次任务一起，则正常进行取盘，反之移库取盘
                        if(hasParentLocationCodes.contains(parentLocation.getLocationCode()) || parentLocation.getTrayCode() == null){
                            WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode());
                            info.put(WmsWcsInfo.TRAY_CODE, tray.getTrayCode());
                            info.put(WmsWcsInfo.START_LOCATION_CODE, tray.getLocationCode());
                            info.put(WmsWcsInfo.START_AREA_CODE, tray.getAreaCode());
                            info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                            infoMap.put(i + size * j, info);
                        }else{
                            List<WmsWcsInfo> childInfoList = new ArrayList<>();//子任务list
                            WmsWcsInfo info = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            info.put(WmsWcsInfo.TRAY_CODE, childLocation.getTrayCode());
                            info.put(WmsWcsInfo.START_AREA_CODE, parentLocation.getAreaId());
                            info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                            info.put(WmsWcsInfo.DOC, outBillCode);

                            //母库位移动至移库库位
                            WmsWcsInfo childInfo = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            childInfo.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                            childInfo.put(WmsWcsInfo.START_LOCATION_CODE, parentLocation.getLocationCode());
                            childInfo.put(WmsWcsInfo.END_LOCATION_CODE, moveLocationCode);
                            childInfo.put(WmsWcsInfo.DOC, outBillCode);
                            childInfoList.add(childInfo);

                            //出库具体任务
                            WmsWcsInfo moveInfo = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NORMAL_OUTBOUND.getCode());
                            moveInfo.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                            moveInfo.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                            moveInfo.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                            moveInfo.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                            moveInfo.put(WmsWcsInfo.DOC, outBillCode);
                            childInfoList.add(moveInfo);

                            //移库库位回至母库位
                            WmsWcsInfo childInfo3 = new WmsWcsInfo(WmsWcsTypeEnum.RELOCATION.getCode(), WmsWcsTaskTypeEnum.MOVE_THE_LIBRARY.getCode());
                            childInfo3.put(WmsWcsInfo.TRAY_CODE, parentLocation.getTrayCode());
                            childInfo3.put(WmsWcsInfo.START_LOCATION_CODE, moveLocationCode);
                            childInfo3.put(WmsWcsInfo.END_LOCATION_CODE, parentLocation.getLocationCode());
                            childInfo3.put(WmsWcsInfo.DOC, outBillCode);

                            childInfoList.add(childInfo3);

                            info.put(WmsWcsInfo.CHILD_INFO_LIST, childInfoList);
                            infoMap.put(i + size * j, info);
                        }
                    }else{
                        throw new ServiceException(trayAreaCode+"库区,请配置移库库位");
                    }
                }
            }
        }
        Set<Integer> integers = infoMap.keySet();
        List<Integer> collect = integers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (Integer integer : collect) {
            infoList.add(infoMap.get(integer));
        }
        if(ObjectUtil.isEmpty(infoList)){
            throw new ServiceException("当前任务不可提交，堆垛机出现故障！");
        }        //分配库位
        String msg = waittaskService.takeTray(infoList);
        //修改出库详情托盘状态为已取出
        LambdaUpdateWrapper<OutbillGoods> outbillGoodsUpdate = Wrappers.lambdaUpdate();
        outbillGoodsUpdate.set(OutbillGoods::getOutBillStatus, OutBillGoodsEnum.TAKEN.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(OutbillGoods::getOutBillCode, outBillCode)
                .in(OutbillGoods::getTrayCode, trayCodeList);
        outbillGoodsMapper.update(null, outbillGoodsUpdate);
        //修改出库抬头状态为拣货中
        LambdaUpdateWrapper<OutBill> outBillUpdate = Wrappers.lambdaUpdate();
        outBillUpdate.set(OutBill::getOutBillStatus, OutBillEnum.PICKING.getCode())
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(OutBill::getOutBillCode, outBillCode);
        outBillMapper.update(null, outBillUpdate);
        //组装离线数据给pda
        List<OutbillVo> returnList = new ArrayList<>();
        LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
        outbillGoodsQuery.select(OutbillGoods::getPartsCode, OutbillGoods::getTrayCode, OutbillGoods::getOutBillCode)
                .eq(OutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(OutbillGoods::getOutBillCode, outBillCode)
                .in(OutbillGoods::getTrayCode, trayCodeList);
        List<OutbillGoods> outbillGoodsList = outbillGoodsMapper.selectList(outbillGoodsQuery);
        for (String trayCode : trayCodeList) {
            returnList.add(getGoodsList(trayCode, outbillGoodsList));
        }
        return AjaxResult.success(msg, returnList);

    }


    /**
     * 拣货出库扫描托盘-PDA
     *
     * @param
     */
    public AjaxResult getTrayGoods(OutbillGoods map) {
        String outBillCode = map.getOutBillCode();//出库单号
        String trayCode = map.getTrayCode();//托盘编号
        LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
        outbillGoodsQuery.select(OutbillGoods::getPartsCode, OutbillGoods::getTrayCode, OutbillGoods::getOutBillCode)
                .eq(OutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(OutbillGoods::getOutBillCode, outBillCode)
                .eq(OutbillGoods::getTrayCode, trayCode);
        List<OutbillGoods> outbillGoodsList = outbillGoodsMapper.selectList(outbillGoodsQuery);
        if (outbillGoodsList.size() == 0) {
            throw new ServiceException("出库单" + outBillCode + "下没有此托盘");
        }
        if (OutBillEnum.ALREADY.getCode().equals(outbillGoodsList.get(0).getOutBillStatus())) {
            throw new ServiceException("托盘" + trayCode + "已出库");
        }
        //组装托盘上货物信息
        OutbillVo vo = getGoodsList(trayCode, outbillGoodsList);
        return AjaxResult.success("成功", vo);
    }


    /**
     * 拣货出库
     *
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult outBill(List<OutbillGoodsDto> mapList) {
        for (OutbillGoodsDto map : mapList) {
            String outBillCode = map.getOutBillCode();//出库单号
            String trayCode = map.getTrayCode();//托盘编号
            List<String> partsCodeList = map.getPartsCodeList();//机件号列表
            //修改出库详情对应托盘数据为已出库
            LambdaUpdateWrapper<OutbillGoods> outbillGoodsUpdate = Wrappers.lambdaUpdate();
            outbillGoodsUpdate.set(OutbillGoods::getOutBillStatus, OutBillGoodsEnum.PICKED.getCode())
                    .eq(OutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(OutbillGoods::getOutBillCode, outBillCode)
                    .eq(OutbillGoods::getTrayCode, trayCode);
            outbillGoodsMapper.update(null, outbillGoodsUpdate);
            //逻辑删除机件码对应库存数据
            LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
            tblstockUpdate.set(BaseEntity::getDelFlag, DelFlagEnum.DEL_YES.getCode())
                    .in(Tblstock::getPartsCode, partsCodeList);
            tblstockMapper.update(null, tblstockUpdate);

            UpdateWrapper<InbillGoods> inbillGoodsUpdateWrapper = new UpdateWrapper<>();
            inbillGoodsUpdateWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
            inbillGoodsUpdateWrapper.in("parts_code",partsCodeList);
            inbillGoodsUpdateWrapper.set("out_status",OutStatusEnum.HAS_OUT.getCode());
            inbillGoodsMapper.update(null,inbillGoodsUpdateWrapper);

            //查询托盘上是否还有货物
            LambdaQueryWrapper<Tblstock> tblstockQuery = Wrappers.lambdaQuery();
            tblstockQuery.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Tblstock::getTrayCode, trayCode);
            if (tblstockMapper.selectCount(tblstockQuery) == 0) {//没有货物，修改托盘状态为空盘
                LambdaUpdateWrapper<Tray> trayUpdate = Wrappers.lambdaUpdate();
                trayUpdate.set(Tray::getEmptyStatus, IsEmptyEnum.ISEMPTY.getCode())
                        .set(Tray::getGoodsCode, null)
                        .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .eq(Tray::getTrayCode, trayCode);
                trayMapper.update(null, trayUpdate);
            }
        }
        //根据详情出库状态，修改出库抬头为已出库
        List<String> outBillCodeList = mapList.stream().map(map -> map.getOutBillCode()).distinct().collect(Collectors.toList());
        for (String outBillCode : outBillCodeList) {
            LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
            outbillGoodsQuery.eq(OutbillGoods::getOutBillCode, outBillCode)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .ne(OutbillGoods::getOutBillStatus, OutBillGoodsEnum.PICKED.getCode());
            if (outbillGoodsMapper.selectCount(outbillGoodsQuery) == 0) {//所有详情状态都为已出库，修改抬头为已出库
                LambdaUpdateWrapper<OutBill> outBillUpdate = Wrappers.lambdaUpdate();
                outBillUpdate.set(OutBill::getOutBillStatus, OutBillEnum.ALREADY.getCode())
                        .set(OutBill::getOutBillTime,new Date())
                        .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .eq(OutBill::getOutBillCode, outBillCode);
                outBillMapper.update(null, outBillUpdate);
                //新增库存台账
                outbillGoodsQuery.clear();
                outbillGoodsQuery.eq(OutbillGoods::getOutBillCode, outBillCode)
                        .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode());
                //出库单数据
                List<OutbillGoods> outbillGoodsList = outbillGoodsMapper.selectList(outbillGoodsQuery);
                //出库单数据根据货物编码、批次去重
                List<OutbillGoods> outbillGoodsDistinct = outbillGoodsList.stream().collect(
                        Collectors. collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getGoodsCode() + ";" + o.getCharg()))), ArrayList::new)
                );
                //新增台账
                List<WmsAccount> accountList = new ArrayList<>();
                for (OutbillGoods outbillGoods:outbillGoodsDistinct) {
                    WmsAccount account = new WmsAccount();
                    account.setId(IdUtil.simpleUUID());
                    account.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    account.setAccountCode(outBillCode);
                    account.setCodeType(AccountEnum.CKD.getCode());
                    account.setCharg(outbillGoods.getCharg());
                    //本次变动数量
                    long count = outbillGoodsList.stream().filter(o -> Objects.equals(o.getGoodsCode(),
                            outbillGoods.getGoodsCode()) && Objects.equals(o.getCharg(), outbillGoods.getCharg()))
                            .count();
                    account.setChangeNum(String.valueOf(count));
                    //结存量
                    LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
                    tblstockQueryWrapper.eq(Tblstock::getGoodsCode,outbillGoods.getGoodsCode())
                            .eq(Tblstock::getCharg,outbillGoods.getCharg())
                            .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    Long aLong = tblstockMapper.selectCount(tblstockQueryWrapper);
                    account.setStockNum(String.valueOf(aLong));
                    account.setGoodsCode(outbillGoods.getGoodsCode());
                    account.setGoodsName(outbillGoods.getGoodsName());
                    account.setModel(outbillGoods.getModel());
                    account.setMeasureUnit(outbillGoods.getMeasureUnit());
                    accountList.add(account);
                }
                if (CollUtil.isNotEmpty(accountList)){
                    wmsAccountService.saveBatch(accountList,accountList.size());
                }
            }
        }
        return AjaxResult.success("成功");
    }

    /**
     * 打印数据
     *
     * @param outBillCode
     * @return
     */
    public List<OutbillGoods> listByOutBillCode(String outBillCode) {
        return outbillGoodsMapper.listByOutBillCode(outBillCode);
    }

    /**
     * 出库后上架
     *
     * @param wmsWcsInfo
     */
    public WmsWcsInfo putOnTray(WmsWcsInfo wmsWcsInfo) {
        String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
        String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编号
        LambdaQueryWrapper<Location> locationQuery = Wrappers.lambdaQuery();
        locationQuery.select(Location::getLocationName,Location::getAreaId,Location::getWarehouseId)
                .eq(Location::getLocationCode,endLocationCode)
                .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .last("limit 1");
        Location location = locationService.getBaseMapper().selectOne(locationQuery);//end库位
        String areaCode = location.getAreaId();//库区编码
        LambdaQueryWrapper<Area> areaQuery = Wrappers.lambdaQuery();
        areaQuery.select(Area::getAreaName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Area::getAreaCode, areaCode)
                .last("limit 1");
        String areaName = areaMapper.selectOne(areaQuery).getAreaName();//库区名称
        String warehouseCode = location.getWarehouseId();//仓库编码
        LambdaQueryWrapper<Warehouse> warehouseQuery = Wrappers.lambdaQuery();
        warehouseQuery.select(Warehouse::getWarehouseName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Warehouse::getWarehouseCode, warehouseCode)
                .last("limit 1");
        String warehouseName = warehouseMapper.selectOne(warehouseQuery).getWarehouseName();//仓库名称
        //查询托盘中剩余货物信息并更新数据的货位等信息
        LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
        tblstockUpdate.set(Tblstock::getUpdateTime, new Date())
                .set(Tblstock::getLocationCode, endLocationCode)
                .set(Tblstock::getLocationName, location.getLocationName())
                .set(Tblstock::getAreaCode, areaCode)
                .set(Tblstock::getAreaName, areaName)
                .set(Tblstock::getWarehouseCode, warehouseCode)
                .set(Tblstock::getWarehouseName, warehouseName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tblstock::getTrayCode, trayCode);
        tblstockMapper.update(null, tblstockUpdate);
        return wmsWcsInfo;
    }

    /**
     * 取盘后更新库存
     *
     * @param wmsWcsInfo
     */
    public WmsWcsInfo takeUpdateTblstock(WmsWcsInfo wmsWcsInfo) {
        String trayCode = (String) wmsWcsInfo.get(WmsWcsInfo.TRAY_CODE);//托盘编号
        String endLocationCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_LOCATION_CODE);//end库位编号
        String locationName = null;//库位名称
        String endAreaCode = (String) wmsWcsInfo.get(WmsWcsInfo.END_AREA_CODE);//end库区编号
        if ("csd".equals(endLocationCode)){
            endLocationCode = null;
        }else{
            LambdaQueryWrapper<Location> locationQuery = Wrappers.lambdaQuery();
            locationQuery.select(Location::getLocationName)
                    .eq(Location::getLocationCode,endLocationCode)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                    .last("limit 1");
            locationName = locationService.getBaseMapper().selectOne(locationQuery).getLocationName();
        }
        LambdaQueryWrapper<Area> areaQuery = Wrappers.lambdaQuery();
        if(endAreaCode.equals("csd")){
            areaQuery.select(Area::getAreaName)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Area::getAreaCode,  (String) wmsWcsInfo.get(WmsWcsInfo.START_AREA_CODE))
                    .last("limit 1");
        }else{
            areaQuery.select(Area::getAreaName)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Area::getAreaCode, endAreaCode)
                    .last("limit 1");
        }

        String areaName = areaMapper.selectOne(areaQuery).getAreaName();//库区名称

        //查询托盘中剩余货物信息并更新数据的货位等信息
        LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
        tblstockUpdate.set(Tblstock::getUpdateTime, new Date())
                .set(Tblstock::getLocationCode, endLocationCode)
                .set(Tblstock::getLocationName, locationName)
                .set(Tblstock::getAreaCode, endAreaCode)
                .set(Tblstock::getAreaName, areaName)
                .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(Tblstock::getTrayCode, trayCode);
        tblstockMapper.update(null, tblstockUpdate);
        return wmsWcsInfo;
    }


    /**
     * PDA-拉取离线数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult pullData() {
        List<OutbillVo> returnList = new ArrayList<>();
        LambdaQueryWrapper<OutbillGoods> outbillGoodsQuery = Wrappers.lambdaQuery();
        outbillGoodsQuery.select(OutbillGoods::getPartsCode, OutbillGoods::getTrayCode, OutbillGoods::getOutBillCode)
                .eq(OutbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .in(OutbillGoods::getOutBillStatus,OutBillGoodsEnum.WAIT.getCode(),OutBillGoodsEnum.TAKEN.getCode());
        List<OutbillGoods> outbillGoodsList = outbillGoodsMapper.selectList(outbillGoodsQuery);
        if (CollUtil.isNotEmpty(outbillGoodsList)){
            //出庫單號list
            List<String> outBillCodeList = outbillGoodsList.stream().map(OutbillGoods::getOutBillCode).distinct().collect(Collectors.toList());
            for (String outBillCode:outBillCodeList) {
                //出库单号所属的出库单货物list
                List<OutbillGoods> outbillGoodsList2 = outbillGoodsList.stream().filter(o -> Objects.equals(outBillCode, o.getOutBillCode())).collect(Collectors.toList());
                //出库单号下面的托盘list
                List<String> trayCodeList = outbillGoodsList2.stream().map(OutbillGoods::getTrayCode).distinct().collect(Collectors.toList());
                for (String trayCode : trayCodeList) {
                    returnList.add(getGoodsList(trayCode, outbillGoodsList2));
                }
            }
        }
        return AjaxResult.success("成功",returnList);
    }


}
