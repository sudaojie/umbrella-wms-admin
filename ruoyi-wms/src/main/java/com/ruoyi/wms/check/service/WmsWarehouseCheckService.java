package com.ruoyi.wms.check.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.EnableStatus;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.PdfUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.wms.basics.domain.Area;
import com.ruoyi.wms.basics.domain.Location;
import com.ruoyi.wms.basics.domain.Temp;
import com.ruoyi.wms.basics.mapper.LocationMapper;
import com.ruoyi.wms.basics.service.AreaService;
import com.ruoyi.wms.basics.service.TempService;
import com.ruoyi.wms.basics.vo.SelectedVo;
import com.ruoyi.wms.check.domain.Check;
import com.ruoyi.wms.check.domain.CheckConfig;
import com.ruoyi.wms.check.domain.CheckDetail;
import com.ruoyi.wms.check.domain.CheckGoods;
import com.ruoyi.wms.check.dto.CheckDetailVo;
import com.ruoyi.wms.check.mapper.CheckConfigMapper;
import com.ruoyi.wms.check.mapper.CheckGoodsMapper;
import com.ruoyi.wms.check.mapper.WmsWarehouseCheckDetailMapper;
import com.ruoyi.wms.check.mapper.WmsWarehouseCheckMapper;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.utils.SerialCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存盘点Service接口
 *
 * @author ruoyi
 * @date 2023-03-15
 */
@Slf4j
@Service
public class WmsWarehouseCheckService extends ServiceImpl<WmsWarehouseCheckMapper, Check> {

    @Autowired
    private AreaService areaService;

    @Autowired(required = false)
    private WmsWarehouseCheckMapper wmsWarehouseCheckMapper;

    @Autowired(required = false)
    private WmsWarehouseCheckDetailMapper wmsWarehouseCheckDetailMapper;

    @Autowired(required = false)
    private CheckGoodsMapper checkGoodsMapper;

    @Autowired(required = false)
    private CheckConfigMapper checkConfigMapper;

    @Autowired
    protected Validator validator;

    @Autowired
    private SerialCodeUtils serialCodeUtils;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TempService tempService;

    @Autowired
    private LocationMapper locationMapper;

    /**
     * 查询库存盘点
     *
     * @param checkBillCode 库存盘点单号
     * @return 库存盘点
     */
    public Check selectWmsWarehouseCheckById(String checkBillCode){
        CheckGoods checkGoods = new CheckGoods();
        checkGoods.setCheckBillCode(checkBillCode);
        QueryWrapper<Check> queryWrapper = getCheckByCodeQueryWrapper(checkGoods);
        Check check = wmsWarehouseCheckMapper.selectCheckByCheckBillCode(queryWrapper);
        check.setLocationList(wmsWarehouseCheckDetailMapper.getLocationList(queryWrapper));
        if (check.getCheckMethod().equals(CheckMethodEnum.NOT.getCode()) || check.getCheckType().equals(CheckTypeEnum.LOCATION.getCode())
                || !check.getCheckStatus().equals(CheckStatusEnum.NOT.getCode())){
            queryWrapper.groupBy("location_code");
            check.setWmsWarehouseCheckDetailList(wmsWarehouseCheckDetailMapper.selectCheckDetailByCode(queryWrapper));
        }else {
            check.setWmsWarehouseCheckDetailList(checkConfigMapper.selectCheckConfigByCode(queryWrapper));
        }
        return check;
    }

    /**
     * 根据ids查询库存盘点
     *
     * @param ids 库存盘点 IDs
     * @return 库存盘点
     */
    public List<Check> selectWmsWarehouseCheckByIds(String[] ids) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsWarehouseCheckMapper.selectList(queryWrapper);
    }
    /**
     * 根据id查询库存盘点
     *
     * @param id 库存盘点 ID
     * @return 库存盘点
     */
    public Check getPrintData(String id) {
        QueryWrapper<Check> queryWrapper = Wrappers.query();
        queryWrapper.eq("check_bill_code", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        Check check = wmsWarehouseCheckMapper.selectCheckByCheckBillCode(queryWrapper);
        Map<String,Object> param = new HashMap<>();
        param.put("del_flag",DelFlagEnum.DEL_NO.getCode());
        param.put("check_bill_code",check.getCheckBillCode());
        List<CheckDetail> details = wmsWarehouseCheckDetailMapper.selectByMap(param);
        check.setWmsWarehouseCheckDetailList(details);
        return check;
    }


    /**
     * 查询库存盘点列表
     *
     * @param wmsWarehouseCheck 库存盘点
     * @return 库存盘点集合
     */
    public List<Check> selectWmsWarehouseCheckList(Check wmsWarehouseCheck){
        QueryWrapper<Check> queryWrapper = getQueryWrapper(wmsWarehouseCheck);
        return wmsWarehouseCheckMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点(全盘)
     *
     * @param checkDetail 库存盘点
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckDetail insertWmsWarehouseCheck(CheckDetail checkDetail){
        //生成盘点单号
        String checkBillCode = serialCodeUtils.getOrderNo(CheckBillNoPrefixEnum.getPrefix(CheckBillNoPrefixEnum.QBPD.getCode()));//盘点单号
        List<CheckDetail> checkDetailList = checkDetail.getCheckDetailsList();
        for (CheckDetail detail:checkDetailList){
            //生成盘点详情
            detail.setId(IdUtil.simpleUUID());
            detail.setCheckBillCode(checkBillCode);
            detail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            wmsWarehouseCheckDetailMapper.insert(detail);
            QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("t.del_flag", DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("t.location_code",detail.getLocationCode());
            List<CheckGoods> checkGoods = checkGoodsMapper.selectByGoodsCode(queryWrapper);
        }
        //生成盘点单
        Check check = new Check();
        check.setCheckBy(checkDetail.getCheckBy());
        check.setId(checkDetail.getId());
        check.setCheckBillCode(checkBillCode);
        check.setCheckStatus(CheckStatusEnum.NOT.getCode());
        check.setCheckMethod(CheckMethodEnum.NOT.getCode());
        check.setCheckType(CheckTypeEnum.LOCATION.getCode());
        check.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        if(StringUtils.isEmpty(checkDetail.getCheckBillCode())){
            wmsWarehouseCheckMapper.insert(check);
        }else{
            wmsWarehouseCheckMapper.updateById(check);
        }
        return checkDetail;
    }

    /**
     * 新增库存盘点(按库位盘)
     *
     * @param checkDetail 库存盘点
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckDetail insertLocationCheck(CheckDetail checkDetail){
        //生成盘点单号
        String checkBillCode = checkDetail.getCheckBillCode()!=null?checkDetail.getCheckBillCode():serialCodeUtils.getOrderNo(CheckBillNoPrefixEnum.getPrefix(CheckBillNoPrefixEnum.HWPD.getCode()));//盘点单号
        List<CheckDetail> checkDetailList = checkDetail.getCheckDetailsList();
        wmsWarehouseCheckDetailMapper.updateByCode(checkDetail.getCheckBillCode());
        checkGoodsMapper.updateGoodsByCode(checkDetail.getCheckBillCode());
        Map<String,Object> map = new HashMap<>();
        map.put("check_bill_code",checkBillCode);
        wmsWarehouseCheckDetailMapper.deleteByMap(map);
        for (CheckDetail detail:checkDetailList){
            //生成盘点详情QQ
            detail.setId(IdUtil.simpleUUID());
            detail.setCheckBillCode(checkBillCode);
            detail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            wmsWarehouseCheckDetailMapper.insert(detail);
            QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("t.del_flag", DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("t.location_code",detail.getLocationCode());
            List<CheckGoods> checkGoods = checkGoodsMapper.selectByGoodsCode(queryWrapper);
            //生成盘点货物
            for (CheckGoods goods:checkGoods){
                goods.setCheckBillCode(detail.getCheckBillCode());
                goods.setCheckDetail(detail.getId());
                goods.setGoodsCode(detail.getGoodsCode());
                goods.setGoodsName(detail.getGoodsName());
                goods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
//                checkGoodsMapper.insert(goods);
            }
        }
        //生成盘点单
        Check check = new Check();
        check.setId(checkDetail.getId());
        check.setCheckBillCode(checkBillCode);
        check.setCheckStatus(CheckStatusEnum.NOT.getCode());
        check.setCheckMethod(CheckMethodEnum.ING.getCode());
        check.setCheckType(CheckTypeEnum.LOCATION.getCode());
        check.setCheckBy(checkDetail.getCheckBy());
        check.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        if(StringUtils.isEmpty(checkDetail.getCheckBillCode())){
            wmsWarehouseCheckMapper.insert(check);
        }else{
            wmsWarehouseCheckMapper.updateById(check);
        }
        return checkDetail;
    }

    /**
     * 新增库存盘点(按货物类型盘)
     *
     * @param checkDetail 库存盘点
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckDetail insertGoodsTypeCheck(CheckDetail checkDetail){
        //生成盘点单号
        String checkBillCode = checkDetail.getCheckBillCode()!=null?checkDetail.getCheckBillCode():serialCodeUtils.getOrderNo(CheckBillNoPrefixEnum.getPrefix(CheckBillNoPrefixEnum.LXPD.getCode()));//盘点单号
        //生成盘点单
        Check check = new Check();
        check.setId(checkDetail.getId());
        check.setCheckBy(checkDetail.getCheckBy());
        check.setCheckBillCode(checkBillCode);
        check.setCheckStatus(CheckStatusEnum.NOT.getCode());
        check.setCheckMethod(CheckMethodEnum.ING.getCode());
        check.setCheckType(CheckTypeEnum.GOODS.getCode());
        check.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        //生成临时盘点表信息
        List<CheckDetail> checkDetailList = checkDetail.getCheckDetailsList();
        checkConfigMapper.updateConfigsByCode(checkDetail.getCheckBillCode());
        for (CheckDetail detail:checkDetailList){
            CheckConfig checkConfig = new CheckConfig();
            checkConfig.setCheckBillCode(checkBillCode);
            checkConfig.setLocationCode(detail.getLocationCode());
            checkConfig.setGoodsCode(detail.getGoodsCode());
            checkConfig.setGoodsName(detail.getGoodsName());
            checkConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            checkConfigMapper.insert(checkConfig);
        }
        if(StringUtils.isEmpty(checkDetail.getCheckBillCode())){
            wmsWarehouseCheckMapper.insert(check);
        }else{
            wmsWarehouseCheckMapper.updateById(check);
        }
        return checkDetail;
    }

    /**
     * 修改库存盘点
     *
     * @param wmsWarehouseCheck 库存盘点
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Check updateWmsWarehouseCheck(Check wmsWarehouseCheck){
        wmsWarehouseCheckMapper.updateById(wmsWarehouseCheck);
        return wmsWarehouseCheck;
    }

    /**
     * 批量删除库存盘点
     *
     * @param ids 需要删除的库存盘点主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseCheckByIds(String[] ids){
        List<Check> wmsWarehouseChecks = new ArrayList<>();
        for (String id : ids) {
            Check wmsWarehouseCheck = new Check();
            wmsWarehouseCheck.setId(id);
            wmsWarehouseCheck.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsWarehouseChecks.add(wmsWarehouseCheck);
            Check check = wmsWarehouseCheckMapper.selectById(id);
            //删除库存盘点详情
            wmsWarehouseCheckDetailMapper.updateByCode(check.getCheckBillCode());
            //删除库存盘点货物单
            checkGoodsMapper.updateGoodsByCode(check.getCheckBillCode());
            //删除库存盘点配置表
            checkConfigMapper.updateConfigsByCode(check.getCheckBillCode());
        }
        return super.updateBatchById(wmsWarehouseChecks) ? 1 : 0;
    }

    /**
     * 删除库存盘点信息
     *
     * @param id 库存盘点主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsWarehouseCheckById(String id){
        Check wmsWarehouseCheck = new Check();
        wmsWarehouseCheck.setId(id);
        wmsWarehouseCheck.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsWarehouseCheckMapper.updateById(wmsWarehouseCheck);
    }

    public QueryWrapper<Check> getQueryWrapper(Check wmsWarehouseCheck) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        if (wmsWarehouseCheck != null) {
            wmsWarehouseCheck.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",wmsWarehouseCheck.getDelFlag());
            //盘点单号
            if (StrUtil.isNotEmpty(wmsWarehouseCheck.getCheckBillCode())) {
                queryWrapper.like("check_bill_code",wmsWarehouseCheck.getCheckBillCode());
            }
            //盘点状态(0.未开始 1.盘点中 2.已完成 )
            if (StrUtil.isNotEmpty(wmsWarehouseCheck.getCheckStatus())) {
                queryWrapper.eq("check_status",wmsWarehouseCheck.getCheckStatus());
            }
            if (CollUtil.isNotEmpty(Arrays.asList(wmsWarehouseCheck.getDaterangeCheckPlanTime()))){
                queryWrapper.gt("create_time",wmsWarehouseCheck.getDaterangeCheckPlanTime()[0]);
                queryWrapper.lt("create_time",wmsWarehouseCheck.getDaterangeCheckPlanTime()[1]);
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    public QueryWrapper<CheckDetail> getAllQueryWrapper() {
        QueryWrapper<CheckDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wl.enable_status", EnableStatus.ENABLE.getCode());
        queryWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.eq("a.del_flag", DelFlagEnum.DEL_NO.getCode());
        //盘点库区类型
        queryWrapper.eq("a.area_type", AreaTypeEnum.CCQ.getCode());
        queryWrapper.groupBy("wl.location_code");
        return queryWrapper;
    }

    public QueryWrapper<Check> getLocationQueryWrapper(Location location) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wl.enable_status", EnableStatus.ENABLE.getCode());
        queryWrapper.eq("wl.del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.eq("a.del_flag", DelFlagEnum.DEL_NO.getCode());
        //盘点库区类型
        queryWrapper.eq("a.area_type", AreaTypeEnum.CCQ.getCode());
        if (StrUtil.isNotEmpty(location.getLocationCode())){
            queryWrapper.like("wl.location_code", location.getLocationCode());
        }
        if (StrUtil.isNotEmpty(location.getAreaId())){
            queryWrapper.eq("wl.area_id", location.getAreaId());
        }
        if (!StrUtil.isEmptyIfStr(location.getPlatoon())){
            queryWrapper.eq("wl.platoon", location.getPlatoon());
        }
        if (!StrUtil.isEmptyIfStr(location.getLayer())){
            queryWrapper.eq("wl.layer", location.getLayer());
        }
        if (!StrUtil.isEmptyIfStr(location.getColumnNum())){
            queryWrapper.eq("wl.column_num", location.getColumnNum());
        }
        queryWrapper.orderByAsc("wl.platoon,wl.layer,wl.column_num");
        return queryWrapper;
    }

    public QueryWrapper<CheckGoods> getGoodsQueryWrapper(CheckGoods checkGoods) {
        QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        if (StrUtil.isNotEmpty(checkGoods.getCheckBillCode())){
            queryWrapper.like("check_bill_code", checkGoods.getCheckBillCode());
        }
        if (StrUtil.isNotEmpty(checkGoods.getGoodsCode())){
            queryWrapper.like("goods_code", checkGoods.getGoodsCode());
        }
        if (StrUtil.isNotEmpty(checkGoods.getLocationCode())){
            queryWrapper.like("location_code", checkGoods.getLocationCode());
        }
        if (StrUtil.isNotEmpty(checkGoods.getOnlyCode())){
            queryWrapper.like("only_code", checkGoods.getOnlyCode());
        }
        if (StrUtil.isNotEmpty(checkGoods.getPartsCode())){
            queryWrapper.like("parts_code", checkGoods.getPartsCode());
        }
        return queryWrapper;
    }

    public QueryWrapper<Check> getDetailQueryWrapper(CheckDetail checkDetail) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        if (StrUtil.isNotEmpty(checkDetail.getCheckBillCode())){
            queryWrapper.like("check_bill_code",checkDetail.getCheckBillCode());
        }
        return queryWrapper;
    }

    /**
     * 获取全盘详细数据信息
     * @return
     */
    public List<CheckDetail> getAllCheckbill() {
        QueryWrapper<CheckDetail> queryWrapper = getAllQueryWrapper();
        //查询移库库位
        List<String> moveList = areaService.getMoveLocationList();
        if(CollUtil.isNotEmpty(moveList)){
            queryWrapper.notIn("wl.location_code",moveList);
        }
        queryWrapper.isNotNull("wl.tray_code");
        queryWrapper.orderByAsc("wl.area_id" ,"wl.location_code","wl.id");
        return wmsWarehouseCheckMapper.getAllCheckbill(queryWrapper);
    }

    /**
     * 查询库位列表
     * @param location
     * @return
     */
    public List<CheckDetailVo> getLocationList(Location location) {
        QueryWrapper<Check> checkQueryWrapper = getLocationQueryWrapper(location);
        //查询移库库位
        List<String> moveList = areaService.getMoveLocationList();
        if(CollUtil.isNotEmpty(moveList)){
            checkQueryWrapper.notIn("wl.location_code",moveList);
        }
        checkQueryWrapper.isNotNull("wl.tray_code");
        PageUtils.startPage();
        return wmsWarehouseCheckMapper.getLocationList(checkQueryWrapper);
    }

    /**
     * 查询货物类型列表
     * @param checkDetail
     * @return
     */
    public List<CheckGoods> getGoodsList(CheckDetail checkDetail) {
        QueryWrapper<Check> checkQueryWrapper = new QueryWrapper<>();
        checkQueryWrapper.eq("t.del_flag",DelFlagEnum.DEL_NO.getCode());
        checkQueryWrapper.eq("a.area_type",AreaTypeEnum.CCQ.getCode());
        if (StringUtils.isNotEmpty(checkDetail.getGoodsCode())){
            checkQueryWrapper.like("t.goods_code",checkDetail.getGoodsCode());
        }
        checkQueryWrapper.orderByAsc("SUBSTR(t.only_code,5,LENGTH(only_code))");
        checkQueryWrapper.groupBy("t.goods_code");
        return wmsWarehouseCheckMapper.getGoodsList(checkQueryWrapper);
    }

    /**
     * 批量删除盘点详情
     * @param ids
     * @return
     */
    public int delCheckDetail(String[] ids) {
        for (int i=0; i<ids.length; i++){
            wmsWarehouseCheckDetailMapper.delCheckDetail(ids[i]);
        }
        return 0;
    }

    /**
     * 批量删除盘点配置
     * @param ids
     * @return
     */
    public int delCheckConfig(String[] ids) {
        for (int i=0; i<ids.length; i++){
            checkConfigMapper.delCheckConfig(ids[i]);

        }
        return 0;
    }

    /**
     * 根据ID获取盘点单详情
     * @param id
     * @return
     */
    public Check getCheckDetailData(String id) {
        Check check = wmsWarehouseCheckMapper.selectById(id);
//        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("check_bill_code", check.getCheckBillCode());
//        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
//        if (check.getCheckMethod().equals(CheckMethodEnum.NOT.getCode()) || check.getCheckType().equals(CheckTypeEnum.LOCATION.getCode())
//                || !check.getCheckStatus().equals(CheckStatusEnum.NOT.getCode())){
//            check.setWmsWarehouseCheckDetailList(wmsWarehouseCheckDetailMapper.selectCheckDetailByCode(queryWrapper));
//        }else {
//            check.setWmsWarehouseCheckDetailList(checkConfigMapper.selectCheckConfigByCode(queryWrapper));
//        }
        return check;
    }

    public QueryWrapper<Check> getCheckByCodeQueryWrapper(CheckGoods checkGoods) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("check_bill_code", checkGoods.getCheckBillCode());
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return queryWrapper;
    }

    /**
     * 查询货物详情列表
     * @param checkGoods
     * @return
     */
    public List<CheckGoods> getCheckGoodsList(CheckGoods checkGoods) {
        List<CheckGoods> goodsList = new ArrayList<>();
        QueryWrapper<CheckGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
        wrapper.eq("check_detail",checkGoods.getCheckDetail());
        wrapper.orderByAsc("goods_code");
        goodsList = checkGoodsMapper.selectGoodsByCode(wrapper);

        return goodsList;
    }



    /**
     * 查询库存明细列表
     * @param checkDetail
     * @return
     */
    public List<CheckDetail> getCheckDetail(CheckDetail checkDetail) {
        QueryWrapper<Check> queryWrapper = getDetailQueryWrapper(checkDetail);
        List<CheckDetail> checkDetailList = new ArrayList<>();
        //判断是否为物资类型列表数据
        if (StringUtils.isNotEmpty(checkDetail.getLocationCode())){
            queryWrapper.like("location_code", checkDetail.getLocationCode());
        }
        if (StringUtils.isNotEmpty(checkDetail.getAreaCode())){
            queryWrapper.like("area_code", checkDetail.getAreaCode());
        }
        queryWrapper.groupBy("location_code");
        queryWrapper.orderByAsc("area_code");
        checkDetailList = wmsWarehouseCheckDetailMapper.selectCheckDetailByCode(queryWrapper);
        return checkDetailList;
    }

    public void printData(Map map, HttpServletResponse responseBody) {
        String tempId = configService.selectConfigByKey("wms.check.tempId");
        Temp temp = tempService.selectWmsWarehouseTempByTempId(tempId);
        SysFile file =  sysFileService.selectSysFileById(temp.getFileKey());
        PdfUtil.createPDF(file.getPath(),map,responseBody);
    }

    public List<CheckDetail> getDetailListData(Check check) {
        QueryWrapper<Check> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        if (StrUtil.isNotEmpty(check.getCheckBillCode())){
            queryWrapper.like("check_bill_code",check.getCheckBillCode());
        }
        List<CheckDetail> checkDetailList = new ArrayList<>();
        //判断是否为物资类型列表数据
        if (check.getCheckMethod().equals(CheckMethodEnum.NOT.getCode()) || check.getCheckType().equals(CheckTypeEnum.LOCATION.getCode())
                || !check.getCheckStatus().equals(CheckStatusEnum.NOT.getCode())){
            queryWrapper.groupBy("location_code");
            checkDetailList = wmsWarehouseCheckDetailMapper.selectCheckDetailByCode(queryWrapper);
        }else {
            checkDetailList = checkConfigMapper.selectCheckConfigByCode(queryWrapper);
        }
        return checkDetailList;
    }

    public List<SelectedVo> getAreaCode() {
        Area area = new Area();
        area.setAreaType(AreaTypeEnum.CCQ.getCode());
        List<SelectedVo> result = areaService.getAreaData(area);
        return  result;
    }

    public Map<String,Object> getLocationData() {
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",DelFlagEnum.DEL_NO.getCode());
        queryWrapper.orderByAsc("platoon,layer,column_num");
        List<Location> locations = locationMapper.selectList(queryWrapper);
        List<String> platoon = locations.stream().map(location ->location.getPlatoon().toString()).distinct().collect(Collectors.toList());
        List<String> layer = locations.stream().map(location -> location.getLayer().toString()).distinct().collect(Collectors.toList());
        List<String> columnNum = locations.stream().map(location -> location.getColumnNum().toString()).distinct().collect(Collectors.toList());
        List<SelectedVo> platoons = new ArrayList<>();
        for (String s:platoon ) {
            SelectedVo vo = new SelectedVo();
            vo.setLabel(s);
            vo.setValue(s);
            platoons.add(vo);
        }
        List<SelectedVo> layers = new ArrayList<>();
        for (String s:layer ) {
            SelectedVo vo = new SelectedVo();
            vo.setLabel(s);
            vo.setValue(s);
            layers.add(vo);
        }
        List<SelectedVo> columnNums = new ArrayList<>();
        for (String s:columnNum ) {
            SelectedVo vo = new SelectedVo();
            vo.setLabel(s);
            vo.setValue(s);
            columnNums.add(vo);
        }
        map.put("platoon",platoons);
        map.put("layer",layers);
        map.put("columnNum",columnNums);
        return map;
    }
}
