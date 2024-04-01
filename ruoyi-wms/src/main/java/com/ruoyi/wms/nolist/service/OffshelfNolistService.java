package com.ruoyi.wms.nolist.service;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.WmsWcsInfo;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.enums.WmsWcsTaskTypeEnum;
import com.ruoyi.common.enums.WmsWcsTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Tray;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.mapper.TrayMapper;
import com.ruoyi.wms.basics.service.TrayService;
import com.ruoyi.wms.basics.vo.LocationMapVo;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.nolist.domain.ListingNolist;
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.ruoyi.wms.nolist.mapper.NolistWaitMapper;
import com.ruoyi.wms.nolist.vo.NoListVo;
import com.ruoyi.wms.outbound.domain.OutbillGoods;
import com.ruoyi.wms.outbound.dto.OutbillGoodsDto;
import com.ruoyi.wms.basics.dto.TrayDto;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.domain.WmsAccount;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.stock.service.WmsAccountService;
import com.ruoyi.wms.utils.SerialCodeUtils;
import com.ruoyi.wms.wcstask.service.WaittaskService;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.nolist.domain.OffshelfNolist;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.nolist.mapper.OffshelfNolistMapper;

import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 无单下架Service接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@Slf4j
@Service
public class OffshelfNolistService extends ServiceImpl<OffshelfNolistMapper, OffshelfNolist> {

    @Autowired
    private OffshelfNolistMapper offshelfNolistMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    protected Validator validator;
    @Autowired
    private TrayMapper trayMapper;
    @Autowired
    private TrayService trayService;
    @Autowired
    private ListingNolistService listingNolistService;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private NolistWaitService nolistWaitService;
    @Autowired
    private NolistWaitMapper nolistWaitMapper;
    @Autowired
    private WaittaskService waittaskService;
    @Autowired
    protected SerialCodeUtils serialCodeUtils;
    @Autowired
    private WmsAccountService wmsAccountService;
    /**
     * 查询无单下架
     *
     * @param id 无单下架主键
     * @return 无单下架
     */
    public OffshelfNolist selectOffshelfNolistById(String id){
        QueryWrapper<OffshelfNolist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return offshelfNolistMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询无单下架
     *
     * @param ids 无单下架 IDs
     * @return 无单下架
     */
    public List<OffshelfNolist> selectOffshelfNolistByIds(String[] ids) {
        QueryWrapper<OffshelfNolist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return offshelfNolistMapper.selectList(queryWrapper);
    }

    /**
     * 查询无单下架列表
     *
     * @param offshelfNolist 无单下架
     * @return 无单下架集合
     */
    public List<OffshelfNolist> selectOffshelfNolistList(OffshelfNolist offshelfNolist){
        QueryWrapper<OffshelfNolist> queryWrapper = getQueryWrapper(offshelfNolist);
        queryWrapper.groupBy("offshelf_Code");
        return offshelfNolistMapper.select(queryWrapper);
    }
    /**
     * 查询无单下架列表
     *
     * @param offshelfNolist 无单下架
     * @return 无单下架集合
     */
    public List<OffshelfNolist> listDetail(OffshelfNolist offshelfNolist){
        QueryWrapper<OffshelfNolist> queryWrapper = getQueryWrapper(offshelfNolist);
        return offshelfNolistMapper.select(queryWrapper);
    }

    /**
     * 新增无单下架
     *
     * @param offshelfNolist 无单下架
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OffshelfNolist insertOffshelfNolist(OffshelfNolist offshelfNolist){
        offshelfNolist.setId(IdUtil.simpleUUID());
        offshelfNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        offshelfNolistMapper.insert(offshelfNolist);
        return offshelfNolist;
    }

    /**
     * 修改无单下架
     *
     * @param offshelfNolist 无单下架
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OffshelfNolist updateOffshelfNolist(OffshelfNolist offshelfNolist){
        offshelfNolistMapper.updateById(offshelfNolist);
        return offshelfNolist;
    }

    /**
     * 批量删除无单下架
     *
     * @param ids 需要删除的无单下架主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOffshelfNolistByIds(String[] ids){
        List<OffshelfNolist> offshelfNolists = new ArrayList<>();
        for (String id : ids) {
            OffshelfNolist offshelfNolist = new OffshelfNolist();
            offshelfNolist.setId(id);
            offshelfNolist.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            offshelfNolists.add(offshelfNolist);
        }
        return super.updateBatchById(offshelfNolists) ? 1 : 0;
    }

    /**
     * 删除无单下架信息
     *
     * @param id 无单下架主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOffshelfNolistById(String id){
        OffshelfNolist offshelfNolist = new OffshelfNolist();
        offshelfNolist.setId(id);
        offshelfNolist.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return offshelfNolistMapper.updateById(offshelfNolist);
    }

    public QueryWrapper<OffshelfNolist> getQueryWrapper(OffshelfNolist wmsOffshelfNolist) {
        QueryWrapper<OffshelfNolist> queryWrapper = new QueryWrapper<>();
        if (wmsOffshelfNolist != null) {
            wmsOffshelfNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsOffshelfNolist.getDelFlag());
            //下架单号
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getOffshelfCode())) {
                queryWrapper.like("offshelf_code",wmsOffshelfNolist.getOffshelfCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getOnlyCode())) {
                queryWrapper.like("only_code",wmsOffshelfNolist.getOnlyCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getMpCode())) {
                queryWrapper.like("mp_code",wmsOffshelfNolist.getMpCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getGoodsCode())) {
                queryWrapper.like("goods_code",wmsOffshelfNolist.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getGoodsName())) {
                queryWrapper.like("goods_name",wmsOffshelfNolist.getGoodsName());
            }
            //规格型号
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getModel())) {
                queryWrapper.like("model",wmsOffshelfNolist.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getMeasureUnit())) {
                queryWrapper.like("measure_unit",wmsOffshelfNolist.getMeasureUnit());
            }
            //批次
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getCharg())) {
                queryWrapper.like("charg",wmsOffshelfNolist.getCharg());
            }
            //库位编号
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getLocationCode())) {
                queryWrapper.like("location_code",wmsOffshelfNolist.getLocationCode());
            }
            //库位名称
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getLocationName())) {
                queryWrapper.like("location_name",wmsOffshelfNolist.getLocationName());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getTrayCode())) {
                queryWrapper.like("tray_code",wmsOffshelfNolist.getTrayCode());
            }
            //下架时间
            if (StringUtils.isNotEmpty(wmsOffshelfNolist.getParams())&&StringUtils.isNotNull(wmsOffshelfNolist.getParams().get("beginOffshelfTime"))) {
                String begin = wmsOffshelfNolist.getParams().get("beginOffshelfTime")+" 00:00:00";
                String end = wmsOffshelfNolist.getParams().get("endOffshelfTime")+" 23:59:59";
                queryWrapper.between("offshelf_time",begin,end);
            }
            //制单人
            if (StrUtil.isNotEmpty(wmsOffshelfNolist.getCreateBy())) {
                queryWrapper.like("create_by",wmsOffshelfNolist.getCreateBy());
            }
            //制单时间
            if (StringUtils.isNotEmpty(wmsOffshelfNolist.getParams())&&StringUtils.isNotNull(wmsOffshelfNolist.getParams().get("beginCreateTime"))) {
                String begin = wmsOffshelfNolist.getParams().get("beginCreateTime")+" 00:00:00";
                String end = wmsOffshelfNolist.getParams().get("endCreateTime")+" 23:59:59";
                queryWrapper.between("create_time",begin,end);
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param offshelfNolistList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<OffshelfNolist> offshelfNolistList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(offshelfNolistList) || offshelfNolistList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (OffshelfNolist offshelfNolist : offshelfNolistList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                OffshelfNolist u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, offshelfNolist);
                    offshelfNolist.setId(IdUtil.simpleUUID());
                    offshelfNolist.setCreateBy(operName);
                    offshelfNolist.setCreateTime(new Date());
                    offshelfNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    offshelfNolistMapper.insert(offshelfNolist);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, offshelfNolist);
                    //todo 验证
                    //int count = offshelfNolistMapper.checkCode(offshelfNolist);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        offshelfNolist.setId(u.getId());
                        offshelfNolist.setUpdateBy(operName);
                        offshelfNolist.setUpdateTime(new Date());
                        offshelfNolistMapper.updateById(offshelfNolist);
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
     * 无单下架取盘
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult takeTray(OutbillGoodsDto map) {
        if(null==map){
            throw new ServiceException("无单下架取盘参数不能为空");
        }
        //结束库区编码（理货区）
        String endAreaCode = map.getEndAreaCode();
        //托盘编号
        List<String> trayCodeList = map.getTrayCodeList();
        if (StringUtils.isEmpty(endAreaCode)||StringUtils.isEmpty(trayCodeList)){
            throw new ServiceException("参数缺失");
        }
        //查询托盘是否可取用
        List<LocationMapVo> trayList = trayMapper.getTrayInfoInTrayCode(AreaTypeEnum.CCQ.getCode(), trayCodeList);
        if (trayCodeList.size() > trayList.size()) {//有不可取用的托盘
            List<String> noTrayCodeList = trayList.stream().map(tray ->tray.getTrayCode()).collect(Collectors.toList());
            List<String> collect = trayCodeList.stream().filter(trayCode -> !noTrayCodeList.contains(trayCode)).collect(Collectors.toList());
            throw new ServiceException("以下托盘不可取：" + collect);
        }else{//校验托盘上的货物是否被锁库存
            QueryWrapper<Tblstock> tblstockQuery = Wrappers.query();
            tblstockQuery.select("distinct tray_code trayCode")
                    .eq("del_flag", DelFlagEnum.DEL_NO.getCode())
                    .eq("lock_status", LockEnum.LOCKED.getCode())
                    .in("tray_code", trayCodeList);
            List<Object> objects = tblstockMapper.selectObjs(tblstockQuery);
            if (objects.size()>0){
                throw new ServiceException("以下托盘上有货物被锁定：" + objects);
            }
        }
        //锁定库位
        List<String> locationCodeList = trayList.stream().map(tray -> tray.getLocationCode()).collect(Collectors.toList());
        LambdaUpdateWrapper<Location> locationUpdate = Wrappers.lambdaUpdate();
        locationUpdate.set(Location::getLockStatus, LockEnum.LOCKED.getCode())
                .in(Location::getLocationCode, locationCodeList);
        locationMapper.update(null, locationUpdate);
        //锁定托盘上的货物
        LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
        tblstockUpdate.set(Tblstock::getLockStatus, LockEnum.LOCKED.getCode())
                .in(Tblstock::getTrayCode, trayCodeList);
        tblstockMapper.update(null, tblstockUpdate);
        //库区编码列表
        List<String> areaCodeList = trayList.stream()
                .map(tray ->  tray.getAreaCode()).distinct().collect(Collectors.toList());
        //库区个数
        int size = areaCodeList.size();
        //交叠组装数据，取盘
        List<WmsWcsInfo> infoList = new ArrayList<>();
        Map<Integer, WmsWcsInfo> infoMap = new HashMap<>();
        for (int i = 0; i < areaCodeList.size(); i++) {
            //库区编码
            String areaCode = areaCodeList.get(i);
            //该库区下的托盘
            List<LocationMapVo> areaTrayList = trayList.stream()
                    .filter(tray -> areaCode.equals(tray.getAreaCode())).collect(Collectors.toList());
            //交叠组装数据
            for (int j = 0; j < areaTrayList.size(); j++) {
                LocationMapVo tray = areaTrayList.get(j);
                WmsWcsInfo info = WmsWcsInfo.getInfo(WmsWcsTypeEnum.TAKETRAY.getCode(), WmsWcsTaskTypeEnum.NO_ORDER.getCode());
                info.put(WmsWcsInfo.TRAY_CODE, (String) tray.getTrayCode());
                info.put(WmsWcsInfo.START_LOCATION_CODE, (String) tray.getLocationCode());
                info.put(WmsWcsInfo.START_AREA_CODE, (String) tray.getAreaCode());
                info.put(WmsWcsInfo.END_AREA_CODE, endAreaCode);
                infoMap.put(i + size * j, info);
            }
        }
        Set<Integer> integers = infoMap.keySet();
        List<Integer> collect = integers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (Integer integer : collect) {
            infoList.add(infoMap.get(integer));
        }
        //分配库位
        String msg = waittaskService.takeTray(infoList);
        //组装离线数据给pda 保存到待上架列表
        List<NoListVo> returnList = new ArrayList<>();
        List<NolistWait> nolistWaitList = new ArrayList<>();
        //查询库存数据
        LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
        tblstockQueryWrapper.select(Tblstock::getPartsCode,Tblstock::getGoodsCode,Tblstock::getTrayCode)
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                .in(Tblstock::getTrayCode,trayCodeList);
        List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockQueryWrapper);
        for (String trayCode : trayCodeList) {
            NoListVo vo = new NoListVo();
            vo.setTrayCode(trayCode);
            //该托盘的库存信息
            List<Tblstock> collect1 = tblstocks.stream().filter(t -> t.getTrayCode().equals(trayCode)).collect(Collectors.toList());
            vo.setPartsCodeList(collect1.stream().map(Tblstock::getPartsCode).collect(Collectors.toList()));
            if (CollUtil.isNotEmpty(collect1)){
                vo.setGoodsCode(collect1.get(0).getGoodsCode());
            }
            returnList.add(vo);
            NolistWait nolistWait = new NolistWait();
            nolistWait.setTrayCode(trayCode);
            nolistWait.setListingStatus(ListingEnum.NOT.getCode());
            nolistWait.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            nolistWaitList.add(nolistWait);
        }
        nolistWaitService.saveBatch(nolistWaitList,nolistWaitList.size());
        return AjaxResult.success(msg, returnList);
    }

    /**
     * 无单上架组盘
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult groupDisk(OutbillGoodsDto map) {
        log.info("无单组盘参数：{}",map);
        List<TrayDto> trayList = map.getTrayList();
        List<String> delPartsCodeList = new ArrayList<>();
        Date now = new Date();//当前时间
        //校验托盘货物
        for (TrayDto trayMap:trayList) {
            String trayCode = trayMap.getTrayCode();//托盘编号
            List<String> partsCodeList = trayMap.getPartsCodeList();//修改后机件列表
            if (partsCodeList.size()>0){
                //校验机件号是否为一种货物类型
                QueryWrapper<Tblstock> tblstockQuery = Wrappers.query();
                tblstockQuery.select("distinct goods_code")
                        .in("parts_code",partsCodeList);
                List<Tblstock> tblstockList = tblstockMapper.selectList(tblstockQuery);
                if (tblstockList.size() >1) {
                    AjaxResult.error("托盘"+trayCode+"上货物不为同一货物类型");
                }
                //校验托盘上货物是否超过堆垛数量
                LambdaQueryWrapper<GoodsInfo> goodsInfoQueryWrapper = Wrappers.lambdaQuery();
                goodsInfoQueryWrapper.select(GoodsInfo::getNum)
                        .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .eq(GoodsInfo::getGoodsCode,tblstockList.get(0).getGoodsCode())
                        .last("limit 1");
                Long num = goodsInfoMapper.selectOne(goodsInfoQueryWrapper).getNum();
                if (partsCodeList.size()>num){
                    AjaxResult.error("托盘"+trayCode+"上货物堆垛数量不可超过"+num);
                }
            }
            List<String> tblPartsCodeList = tblstockMapper.selectPartsCodeByTrayCode(trayCode);//库存机件列表
            trayMap.setAddPartsCodeList(partsCodeList.stream()//增加的机件号列表
                    .filter(partsCode -> !tblPartsCodeList.contains(partsCode)).collect(Collectors.toList()));
            delPartsCodeList.addAll(tblPartsCodeList.stream()//减少的机件号列表
                    .filter(partsCode -> !partsCodeList.contains(partsCode)).collect(Collectors.toList()));
        }
        //下架，逻辑删除库存
        if (delPartsCodeList.size()>0){
            LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
            tblstockUpdate.set(Tblstock::getDelFlag, DelFlagEnum.DEL_YES.getCode())
                    .in(Tblstock::getPartsCode, delPartsCodeList);
            tblstockMapper.update(null, tblstockUpdate);
        }
        //校验新增的货物是否都已下架
        for (TrayDto trayMap:trayList) {
            String trayCode = trayMap.getTrayCode();//托盘编号
            List<String> addPartsCodeList = trayMap.getAddPartsCodeList();//增加的机件号列表
            if (addPartsCodeList.size()>0){
                QueryWrapper<Tblstock> tblstockQuery = Wrappers.query();
                tblstockQuery.select("parts_code")
                        .eq("del_flag",DelFlagEnum.DEL_NO.getCode())
                        .in("parts_code",addPartsCodeList);
                List<Object> objects = tblstockMapper.selectObjs(tblstockQuery);
                if (objects.size()>0){
                    throw new RuntimeException("托盘"+trayCode+"上新增的货物"+objects+"未下架");
                }
            }
        }
        //新增无单下架记录
        if (delPartsCodeList.size()>0){
            String offshelfCode = serialCodeUtils.getOrderNo("XJ");//下架单号
            log.info("无单下架单记录{}下架机件号{}",offshelfCode,delPartsCodeList);
            List<OffshelfNolist> offshelfNolistList = new ArrayList<>();
            for (String partsCode:delPartsCodeList) {
                LambdaQueryWrapper<Tblstock> tblstockQuery = Wrappers.lambdaQuery();
                tblstockQuery.eq(Tblstock::getPartsCode, partsCode)
                    .last("limit 1");
                Tblstock tblstock = tblstockMapper.selectOne(tblstockQuery);
                OffshelfNolist offshelfNolist = new OffshelfNolist();
                offshelfNolist.setOffshelfCode(offshelfCode);
                offshelfNolist.setOnlyCode(tblstock.getOnlyCode());
                offshelfNolist.setMpCode(tblstock.getPartsCode());
                offshelfNolist.setGoodsCode(tblstock.getGoodsCode());
                offshelfNolist.setGoodsName(tblstock.getGoodsName());
                offshelfNolist.setModel(tblstock.getModel());
                offshelfNolist.setMeasureUnit(tblstock.getMeasureUnit());
                offshelfNolist.setCharg(tblstock.getCharg());
                offshelfNolist.setLocationCode(tblstock.getLocationCode());
                offshelfNolist.setLocationName(tblstock.getLocationName());
                offshelfNolist.setTrayCode(tblstock.getTrayCode());
                offshelfNolist.setOffshelfTime(now);
                offshelfNolist.setOffshelfStatus(OffShelfEnum.ALREADY.getCode());
                offshelfNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                offshelfNolistList.add(offshelfNolist);
            }
            if (offshelfNolistList.size()>0){
                this.saveBatch(offshelfNolistList,offshelfNolistList.size());
                //新增库存台账
                //无单下架记录根据货物编码、批次去重
                List<OffshelfNolist> offshelfNolistDistinct = offshelfNolistList.stream().collect(
                        Collectors. collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getGoodsCode() + ";" + o.getCharg()))), ArrayList::new)
                );
                //新增台账
                List<WmsAccount> accountList = new ArrayList<>();
                for (OffshelfNolist noList:offshelfNolistDistinct) {
                    WmsAccount account = new WmsAccount();
                    account.setId(IdUtil.simpleUUID());
                    account.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    account.setAccountCode(offshelfCode);
                    account.setCodeType(AccountEnum.WDXJ.getCode());
                    account.setCharg(noList.getCharg());
                    //本次变动数量
                    long count = offshelfNolistList.stream().filter(o -> Objects.equals(o.getGoodsCode(),
                            noList.getGoodsCode()) && Objects.equals(o.getCharg(), noList.getCharg()))
                            .count();
                    account.setChangeNum(String.valueOf(count));
                    //结存量
                    LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
                    tblstockQueryWrapper.eq(Tblstock::getGoodsCode,noList.getGoodsCode())
                            .eq(Tblstock::getCharg,noList.getCharg())
                            .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    Long aLong = tblstockMapper.selectCount(tblstockQueryWrapper);
                    account.setStockNum(String.valueOf(aLong));
                    account.setGoodsCode(noList.getGoodsCode());
                    account.setGoodsName(noList.getGoodsName());
                    account.setModel(noList.getModel());
                    account.setMeasureUnit(noList.getMeasureUnit());
                    accountList.add(account);
                }
                if (CollUtil.isNotEmpty(accountList)){
                    wmsAccountService.saveBatch(accountList,accountList.size());
                }
            }
        }
        //还原库存 新增无单上架记录
        String listingCode = serialCodeUtils.getOrderNo("SJ");//上架单号
        List<ListingNolist> listingNolistList = new ArrayList<>();
        for (TrayDto trayMap:trayList) {
            String trayCode = trayMap.getTrayCode();//托盘编号
            List<String> addPartsCodeList = trayMap.getAddPartsCodeList();//增加的机件号列表
            if (addPartsCodeList.size()>0){
                log.info("无单上架单记录{}托盘{}上架机件号{}",listingCode,trayCode,addPartsCodeList);
                LambdaUpdateWrapper<Tblstock> tblstockUpdate = Wrappers.lambdaUpdate();
                tblstockUpdate.set(Tblstock::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .set(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                        .set(Tblstock::getTrayCode,trayCode)
                        .in(Tblstock::getPartsCode, addPartsCodeList);
                tblstockMapper.update(null, tblstockUpdate);
                for (String partsCode:addPartsCodeList) {
                    LambdaQueryWrapper<Tblstock> tblstockQuery = Wrappers.lambdaQuery();
                    tblstockQuery.eq(Tblstock::getPartsCode, partsCode)
                            .last("limit 1");
                    Tblstock tblstock = tblstockMapper.selectOne(tblstockQuery);
                    ListingNolist listingNolist = new ListingNolist();
                    listingNolist.setListingCode(listingCode);
                    listingNolist.setOnlyCode(tblstock.getOnlyCode());
                    listingNolist.setMpCode(tblstock.getPartsCode());
                    listingNolist.setGoodsCode(tblstock.getGoodsCode());
                    listingNolist.setGoodsName(tblstock.getGoodsName());
                    listingNolist.setModel(tblstock.getModel());
                    listingNolist.setMeasureUnit(tblstock.getMeasureUnit());
                    listingNolist.setCharg(tblstock.getCharg());
                    listingNolist.setLocationCode(tblstock.getLocationCode());
                    listingNolist.setLocationName(tblstock.getLocationName());
                    listingNolist.setTrayCode(tblstock.getTrayCode());
                    listingNolist.setListingTime(now);
                    listingNolist.setListingStatus(ListingEnum.ALREADY.getCode());
                    listingNolist.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    listingNolistList.add(listingNolist);
                }
            }
        }
        if (listingNolistList.size()>0){
            listingNolistService.saveBatch(listingNolistList);
            //新增库存台账
            //无单上架记录根据货物编码、批次去重
            List<ListingNolist> listingNolistDistinct = listingNolistList.stream().collect(
                    Collectors. collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getGoodsCode() + ";" + o.getCharg()))), ArrayList::new)
            );
            //新增台账
            List<WmsAccount> accountList = new ArrayList<>();
            for (ListingNolist noList:listingNolistDistinct) {
                WmsAccount account = new WmsAccount();
                account.setId(IdUtil.simpleUUID());
                account.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                account.setAccountCode(listingCode);
                account.setCodeType(AccountEnum.WDSJ.getCode());
                account.setCharg(noList.getCharg());
                //本次变动数量
                long count = listingNolistList.stream().filter(o -> Objects.equals(o.getGoodsCode(),
                        noList.getGoodsCode()) && Objects.equals(o.getCharg(), noList.getCharg()))
                        .count();
                account.setChangeNum(String.valueOf(count));
                //结存量
                LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
                tblstockQueryWrapper.eq(Tblstock::getGoodsCode,noList.getGoodsCode())
                        .eq(Tblstock::getCharg,noList.getCharg())
                        .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                Long aLong = tblstockMapper.selectCount(tblstockQueryWrapper);
                account.setStockNum(String.valueOf(aLong));
                account.setGoodsCode(noList.getGoodsCode());
                account.setGoodsName(noList.getGoodsName());
                account.setModel(noList.getModel());
                account.setMeasureUnit(noList.getMeasureUnit());
                accountList.add(account);
            }
            if (CollUtil.isNotEmpty(accountList)){
                wmsAccountService.saveBatch(accountList,accountList.size());
            }
        }
        //修改托盘状态
        for (TrayDto trayMap:trayList) {
            String trayCode = trayMap.getTrayCode();//托盘编号
            LambdaQueryWrapper<Tblstock> tblstockQuery = Wrappers.lambdaQuery();
            tblstockQuery.select(Tblstock::getGoodsCode)
                    .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(Tblstock::getTrayCode, trayCode);
            List<Tblstock> tblstockList = tblstockMapper.selectList(tblstockQuery);
            LambdaUpdateWrapper<Tray> trayUpdate = Wrappers.lambdaUpdate();
            if (tblstockList.size() == 0) {//没有货物，修改托盘状态为空盘
                trayUpdate.set(Tray::getEmptyStatus, IsEmptyEnum.ISEMPTY.getCode())
                        .set(Tray::getGoodsCode, null)
                        .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .eq(Tray::getTrayCode, trayCode);
                trayMapper.update(null, trayUpdate);
            }else {//修改托盘为非空盘
                trayUpdate.set(Tray::getEmptyStatus, IsEmptyEnum.NOTEMPTY.getCode())
                        .set(Tray::getGoodsCode, tblstockList.get(0).getGoodsCode())
                        .eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .eq(Tray::getTrayCode, trayCode);
                trayMapper.update(null, trayUpdate);
            }
        }
        return AjaxResult.success("成功");
    }


    /**
     * PDA-拉取离线数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult pullData() {
        List<NoListVo> returnList = new ArrayList<>();
        LambdaQueryWrapper<NolistWait> nolistWaitQueryWrapper = Wrappers.lambdaQuery();
        nolistWaitQueryWrapper.select(NolistWait::getTrayCode)
                .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode());
        List<String> trayCodeList = nolistWaitMapper.selectObjs(nolistWaitQueryWrapper).stream().map(String::valueOf).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(trayCodeList)){
            LambdaQueryWrapper<Tblstock> tblstockQueryWrapper = Wrappers.lambdaQuery();
            tblstockQueryWrapper.select(Tblstock::getPartsCode,Tblstock::getGoodsCode,Tblstock::getTrayCode)
                    .eq(BaseEntity::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getTrayCode,trayCodeList);
            List<Tblstock> tblstocks = tblstockMapper.selectList(tblstockQueryWrapper);
            for (String trayCode : trayCodeList) {
                NoListVo vo = new NoListVo();
                vo.setTrayCode(trayCode);
                //该托盘的库存信息
                List<Tblstock> collect1 = tblstocks.stream().filter(t -> t.getTrayCode().equals(trayCode)).collect(Collectors.toList());
                vo.setPartsCodeList(collect1.stream().map(Tblstock::getPartsCode).collect(Collectors.toList()));
                if (CollUtil.isNotEmpty(collect1)){
                    vo.setGoodsCode(collect1.get(0).getGoodsCode());
                }
                returnList.add(vo);
            }
        }
        return AjaxResult.success("成功",returnList);
    }

}
