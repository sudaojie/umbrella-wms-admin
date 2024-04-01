package com.ruoyi.wms.move.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.enums.LockEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.enums.ManMadeEnum;
import com.ruoyi.wms.enums.MoveEnum;
import com.ruoyi.wms.move.domain.WmsMoveDetail;
import com.ruoyi.wms.move.domain.WmsMoveDetailGoods;
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.ruoyi.wms.move.mapper.WmsMoveDetailMapper;
import com.ruoyi.wms.move.mapper.WmsMoveListMapper;
import com.ruoyi.wms.move.vo.WmsMoveDetailVo;
import com.ruoyi.wms.stock.domain.Tblstock;
import com.ruoyi.wms.stock.mapper.TblstockMapper;
import com.ruoyi.wms.utils.SerialCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 移库单Service接口
 *
 * @author nf
 * @date 2023-03-01
 */
@Slf4j
@Service
public class WmsMoveListService extends ServiceImpl<WmsMoveListMapper, WmsMoveList> {

    @Autowired
    private WmsMoveListMapper wmsMoveListMapper;
    @Autowired
    private WmsMoveDetailGoodsService wmsMoveDetailGoodsService;
    @Autowired
    private WmsMoveDetailMapper wmsMoveDetailMapper;
    @Autowired
    private TblstockMapper tblstockMapper;
    @Autowired
    private WmsMoveDetailService wmsMoveDetailService;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    protected Validator validator;
    @Autowired
    protected SerialCodeUtils serialCodeUtils;
    /**
     * 查询移库单
     *
     * @param id 移库单主键
     * @return 移库单
     */
    public WmsMoveList selectWmsMoveListById(String id){
        QueryWrapper<WmsMoveList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        WmsMoveList wmsMoveList = wmsMoveListMapper.selectOne(queryWrapper);
        List<WmsMoveDetail> details = wmsMoveDetailService.selectWmsMoveDetailList(new WmsMoveDetail().setMoveCode(wmsMoveList.getMoveCode()));
        wmsMoveList.setWmsMoveDetailList(details);
        return wmsMoveList;
    }


    /**
     * 根据ids查询移库单
     *
     * @param ids 移库单 IDs
     * @return 移库单
     */
    public List<WmsMoveList> selectWmsMoveListByIds(String[] ids) {
        QueryWrapper<WmsMoveList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsMoveListMapper.selectList(queryWrapper);
    }

    /**
     * 查询移库单列表
     *
     * @param wmsMoveList 移库单
     * @return 移库单集合
     */
    public List<WmsMoveList> selectWmsMoveListList(WmsMoveList wmsMoveList){
        QueryWrapper<WmsMoveList> queryWrapper = getQueryWrapper(wmsMoveList);
        return wmsMoveListMapper.select(queryWrapper);
    }

    /**
     * 新增移库单
     *
     * @param wmsMoveList 移库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveList insertWmsMoveList(WmsMoveList wmsMoveList){
        wmsMoveList.setId(IdUtil.simpleUUID());
        wmsMoveList.setMoveStatus(MoveEnum.unexecuted.getCode());
        String code = serialCodeUtils.getOrderNo("YK");
        wmsMoveList.setMoveCode(code);
        wmsMoveList.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        if(StringUtils.isNotEmpty(wmsMoveList.getWmsMoveDetailList())){
            List<WmsMoveDetail> detailList = wmsMoveList.getWmsMoveDetailList();
            List<String> locations = new ArrayList<>();
            for (WmsMoveDetail detail:detailList) {
                detail.setId(IdUtils.simpleUUID());
                detail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                detail.setMoveCode(code);
                detail.setManMadeStatus(ManMadeEnum.NOT.getCode());
                detail.setMoveStatus(MoveEnum.unexecuted.getCode());
                locations.add(detail.getInLocationCode());
                locations.add(detail.getOutLocationCode());
            }
            //判断是否有被使用的库位
            List<Location> locked = locationMapper.isLocked(locations);
            if(CollectionUtil.isNotEmpty(locked)){
                String result = locked.stream().map(location->location.getLocationCode()).collect(Collectors.joining(","));
                throw new RuntimeException(result+"库位已被占用，请重新维护数据");
            }
            //判断是否有库存数据被锁定
            LambdaQueryWrapper<Tblstock> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                    .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .in(Tblstock::getLocationCode,locations);
            List<Tblstock> lockedList = tblstockMapper.selectList(queryWrapper);
            if(CollectionUtil.isNotEmpty(lockedList)){
                String codes = lockedList.stream().map(tblstock -> tblstock.getLocationCode()).distinct().collect(Collectors.joining(","));
                throw new ServiceException("库位【"+codes+"】上存在库存数据被使用，无法创建移库单");
            }else{
                //根据库位锁定库存数据
                LambdaUpdateWrapper<Tblstock> updateWrapper=Wrappers.lambdaUpdate();
                updateWrapper.set(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                        .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(Tblstock::getLocationCode,locations);
                tblstockMapper.update(null,updateWrapper);
            }
            locationMapper.lockLocation(locations);
            wmsMoveDetailService.saveBatch(detailList);
        }
        wmsMoveListMapper.insert(wmsMoveList);
        return wmsMoveList;
    }

    /**
     * 修改移库单
     *
     * @param wmsMoveList 移库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveList updateWmsMoveList(WmsMoveList wmsMoveList){
        if(StringUtils.isEmpty(wmsMoveList.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        if(CollectionUtil.isNotEmpty(wmsMoveList.getIds())){
            wmsMoveDetailService.deleteWmsMoveDetailByIds(wmsMoveList.getIds().toArray(new String[wmsMoveList.getIds().size()]));
        }
        wmsMoveListMapper.updateById(wmsMoveList);
        List<WmsMoveDetail> detailList = wmsMoveList.getWmsMoveDetailList();
        if(StringUtils.isNotEmpty(detailList)){
            List<String> ids = new ArrayList<>();
            List<WmsMoveDetail> oldDetail = new ArrayList<>();
            List<WmsMoveDetail> newDetail = new ArrayList<>();
            List<String> locations = new ArrayList<>();
            for (WmsMoveDetail detail:detailList) {
                if(StringUtils.isNotEmpty(detail.getId())){
                    ids.add(detail.getId());
                    oldDetail.add(detail);
                    locations.add(detail.getInLocationCode());
                }else{
                    detail.setId(IdUtils.simpleUUID());
                    detail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    detail.setMoveCode(wmsMoveList.getMoveCode());
                    detail.setManMadeStatus(ManMadeEnum.NOT.getCode());
                    detail.setMoveStatus(MoveEnum.unexecuted.getCode());
                    newDetail.add(detail);
                    locations.add(detail.getInLocationCode());
                    locations.add(detail.getOutLocationCode());
                }
            }
            if(ids.size()>0){
                List<WmsMoveDetail> oldDetail1 = wmsMoveDetailService.selectWmsMoveDetailByIds(ids.toArray(new String[ids.size()]));
                List<String> oldLocations = oldDetail1.stream().map(detail -> detail.getInLocationCode()).collect(Collectors.toList());
                if(oldLocations.size()>0){
                    LambdaUpdateWrapper<Location> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                            .eq(Location::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                            .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                            .eq(Location::getLockStatus, LockEnum.LOCKED.getCode())
                            .in(Location::getLocationCode,oldLocations);
                    locationMapper.update(null,updateWrapper);
                }
            }
            if(locations.size()>0){
                //判断是否有被使用的库位
                List<Location> locked = locationMapper.isLocked(locations);
                if(locked.size()>0){
                    String result = locked.stream().map(location->location.getLocationCode()).collect(Collectors.joining(","));
                    throw new RuntimeException(result+"库位已被占用，请重新维护数据");
                }
                //判断是否有库存数据被锁定
                LambdaQueryWrapper<Tblstock> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                        .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(Tblstock::getLocationCode,locations);
                List<Tblstock> lockedList = tblstockMapper.selectList(queryWrapper);
                if(CollectionUtil.isNotEmpty(lockedList)){
                    String codes = lockedList.stream().map(tblstock -> tblstock.getLocationCode()).distinct().collect(Collectors.joining(","));
                    throw new ServiceException("库位【"+codes+"】上存在库存数据被使用，无法创建移库单");
                }else{
                    //根据库位锁定库存数据
                    LambdaUpdateWrapper<Tblstock> updateWrapper=Wrappers.lambdaUpdate();
                    updateWrapper.set(Tblstock::getLockStatus,LockEnum.LOCKED.getCode())
                            .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                            .in(Tblstock::getLocationCode,locations);
                    tblstockMapper.update(null,updateWrapper);
                }
                locationMapper.lockLocation(locations);
            }
            if(newDetail.size()>0){
                wmsMoveDetailService.saveBatch(newDetail);
            }
            if(oldDetail.size()>0){
                wmsMoveDetailService.updateBatchById(oldDetail);
            }
        }
        return wmsMoveList;
    }

    /**
     * 批量删除移库单
     *
     * @param ids 需要删除的移库单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveListByIds(String[] ids){
        List<WmsMoveList> wmsMoveLists = new ArrayList<>();
        for (String id : ids) {
            WmsMoveList wmsMoveList = new WmsMoveList();
            wmsMoveList.setId(id);
            wmsMoveList.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsMoveLists.add(wmsMoveList);
        }
        List<WmsMoveList> moveLists = selectWmsMoveListByIds(ids);
        for (WmsMoveList move:moveLists) {
            WmsMoveDetail detail = new WmsMoveDetail();
            detail.setMoveCode(move.getMoveCode());
            List<WmsMoveDetail> details = wmsMoveDetailService.selectWmsMoveDetailList(detail);
            List<String> locations = new ArrayList<>();
            for (WmsMoveDetail d:details) {
                d.setDelFlag(DelFlagEnum.DEL_YES.getCode());
                locations.add(d.getOutLocationCode());
                locations.add(d.getInLocationCode());
            }
            if(locations.size()>0){
                LambdaUpdateWrapper<Location> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(Location::getLockStatus, LockEnum.NOTLOCK.getCode())
                            .eq(Location::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                            .eq(Location::getEnableStatus, EnableStatus.ENABLE.getCode())
                            .in(Location::getLocationCode,locations);
                //根据库位解锁库存数据
                LambdaUpdateWrapper<Tblstock> updateWrapper1=Wrappers.lambdaUpdate();
                updateWrapper1.set(Tblstock::getLockStatus,LockEnum.NOTLOCK.getCode())
                        .eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                        .in(Tblstock::getLocationCode,locations);
                tblstockMapper.update(null,updateWrapper1);
                locationMapper.update(null,updateWrapper);
            }
            wmsMoveDetailService.updateBatchById(details);
        }
        return super.updateBatchById(wmsMoveLists) ? 1 : 0;
    }

    /**
     * 删除移库单信息
     *
     * @param id 移库单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsMoveListById(String id){
        WmsMoveList wmsMoveList = new WmsMoveList();
        wmsMoveList.setId(id);
        wmsMoveList.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsMoveListMapper.updateById(wmsMoveList);
    }

    public QueryWrapper<WmsMoveList> getQueryWrapper(WmsMoveList wmsMoveList) {
        QueryWrapper<WmsMoveList> queryWrapper = new QueryWrapper<>();
        if (wmsMoveList != null) {
            wmsMoveList.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsMoveList.getDelFlag());
            //移库单号
            if (StrUtil.isNotEmpty(wmsMoveList.getMoveCode())) {
                queryWrapper.eq("move_code",wmsMoveList.getMoveCode());
            }
            //移库状态
            if (StrUtil.isNotEmpty(wmsMoveList.getMoveStatus())) {
                queryWrapper.eq("move_status",wmsMoveList.getMoveStatus());
            }
            //创建时间
            if (StringUtils.isNotEmpty(wmsMoveList.getParams())&&StringUtils.isNotNull(wmsMoveList.getParams().get("beginCreateTime"))) {
                String begin = wmsMoveList.getParams().get("beginCreateTime")+" 00:00:00";
                String end = wmsMoveList.getParams().get("endCreateTime")+" 23:59:59";
                queryWrapper.between("create_time",begin,end);
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param wmsMoveListList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<WmsMoveList> wmsMoveListList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsMoveListList) || wmsMoveListList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsMoveList wmsMoveList : wmsMoveListList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsMoveList u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsMoveList);
                    wmsMoveList.setId(IdUtil.simpleUUID());
                    wmsMoveList.setCreateBy(operName);
                    wmsMoveList.setCreateTime(new Date());
                    wmsMoveList.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsMoveListMapper.insert(wmsMoveList);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsMoveList);
                    //todo 验证
                    //int count = wmsMoveListMapper.checkCode(wmsMoveList);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        wmsMoveList.setId(u.getId());
                        wmsMoveList.setUpdateBy(operName);
                        wmsMoveList.setUpdateTime(new Date());
                        wmsMoveListMapper.updateById(wmsMoveList);
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
     * 开始移库单
     *
     * @param wmsMoveList 移库单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsMoveList updateStatus(WmsMoveList wmsMoveList){
        if(StringUtils.isEmpty(wmsMoveList.getId())){
            throw new ServiceException("修改失败，数据主键id缺失");
        }
        WmsMoveDetail detail = new WmsMoveDetail();
        detail.setMoveCode(wmsMoveList.getMoveCode());
        List<WmsMoveDetail> details = wmsMoveDetailService.selectWmsMoveDetailList(detail);
        if(details.isEmpty()){
            throw new RuntimeException("该移库单未创建移库任务");
        }
        //wmsMoveListMapper.updateById(wmsMoveList);
        WmsMoveDetailVo map = new WmsMoveDetailVo();
        map.setMoveCode(wmsMoveList.getMoveCode());
        if(MoveEnum.executing.getCode().equals(wmsMoveList.getMoveStatus())){
            //开始移库任务
            AjaxResult result= wmsMoveDetailService.startMoveTake(map);
            //添加数据到详情货物表
            List<WmsMoveDetailGoods> detailGoods = new ArrayList<>();
            WmsMoveDetailGoods goods = null;
            for (WmsMoveDetail moveDetail :details) {
                //查询数据
                LambdaQueryWrapper<Tblstock> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Tblstock::getTrayCode,moveDetail.getOutTrayCode()).eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                List<Tblstock> outTblstock = tblstockMapper.selectList(queryWrapper);
                for (Tblstock tblstock:outTblstock) {
                    goods = new WmsMoveDetailGoods();
                    goods.setId(IdUtils.simpleUUID());
                    goods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    goods.setGoodsCode(tblstock.getGoodsCode());
                    goods.setMoveCode(wmsMoveList.getMoveCode());
                    goods.setGoodsName(tblstock.getGoodsName());
                    goods.setModel(tblstock.getModel());
                    goods.setTrayCode(tblstock.getTrayCode());
                    goods.setMeasureUnit(tblstock.getMeasureUnit());
                    goods.setOnlyCode(tblstock.getOnlyCode());
                    goods.setMpCode(tblstock.getPartsCode());
                    goods.setCharg(tblstock.getCharg());
                    goods.setOutWarehouseCode(tblstock.getWarehouseCode());
                    goods.setOutWarehouseName(tblstock.getWarehouseName());
                    goods.setOutAreaCode(tblstock.getAreaCode());
                    goods.setOutAreaName(tblstock.getAreaName());
                    goods.setOutLocationCode(tblstock.getLocationCode());
                    goods.setOutLocationName(tblstock.getLocationName());
                    goods.setInWarehouseCode(moveDetail.getInWarehouseCode());
                    goods.setInWarehouseName(moveDetail.getInWarehouseName());
                    goods.setInAreaCode(moveDetail.getInAreaCode());
                    goods.setInAreaName(moveDetail.getInAreaName());
                    goods.setInLocationCode(moveDetail.getInLocationCode());
                    goods.setInLocationName(moveDetail.getInLocationName());
                    detailGoods.add(goods);
                }
                queryWrapper.clear();
                if(StringUtils.isNotEmpty(moveDetail.getInTrayCode())){
                    queryWrapper.eq(Tblstock::getTrayCode,moveDetail.getInTrayCode()).eq(Tblstock::getDelFlag,DelFlagEnum.DEL_NO.getCode());
                    List<Tblstock> inTblstock = tblstockMapper.selectList(queryWrapper);
                    for (Tblstock tblstock:inTblstock) {
                        goods = new WmsMoveDetailGoods();
                        goods.setId(IdUtils.simpleUUID());
                        goods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                        goods.setGoodsCode(tblstock.getGoodsCode());
                        goods.setMoveCode(wmsMoveList.getMoveCode());
                        goods.setGoodsName(tblstock.getGoodsName());
                        goods.setModel(tblstock.getModel());
                        goods.setTrayCode(tblstock.getTrayCode());
                        goods.setMeasureUnit(tblstock.getMeasureUnit());
                        goods.setOnlyCode(tblstock.getOnlyCode());
                        goods.setMpCode(tblstock.getPartsCode());
                        goods.setCharg(tblstock.getCharg());
                        goods.setOutWarehouseCode(tblstock.getWarehouseCode());
                        goods.setOutWarehouseName(tblstock.getWarehouseName());
                        goods.setOutAreaCode(tblstock.getAreaCode());
                        goods.setOutAreaName(tblstock.getAreaName());
                        goods.setOutLocationCode(tblstock.getLocationCode());
                        goods.setOutLocationName(tblstock.getLocationName());
                        goods.setInWarehouseCode(moveDetail.getOutWarehouseCode());
                        goods.setInWarehouseName(moveDetail.getOutWarehouseName());
                        goods.setInAreaCode(moveDetail.getOutAreaCode());
                        goods.setInAreaName(moveDetail.getOutAreaName());
                        goods.setInLocationCode(moveDetail.getOutLocationCode());
                        goods.setInLocationName(moveDetail.getOutLocationName());
                        detailGoods.add(goods);
                    }
                }
            }
            wmsMoveDetailGoodsService.saveBatch(detailGoods);

        }
        return wmsMoveList;
    }
}
