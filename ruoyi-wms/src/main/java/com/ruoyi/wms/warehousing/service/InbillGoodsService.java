package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.InBillStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CodeGeneratorUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import com.ruoyi.wms.basics.dto.PrintDataDto;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
import com.ruoyi.wms.enums.GoodsInfoEnum;
import com.ruoyi.wms.enums.InbillGoodsEnum;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import com.ruoyi.wms.warehousing.dto.InbillGoodsDto;
import com.ruoyi.wms.warehousing.dto.PartsPrintDto;
import com.ruoyi.wms.warehousing.mapper.InBillMapper;
import com.ruoyi.wms.warehousing.mapper.InbillDetailMapper;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.warehousing.mapper.PartsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库单货物Service接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Slf4j
@Service
public class InbillGoodsService extends ServiceImpl<InbillGoodsMapper, InbillGoods> {

    @Autowired
    private InbillGoodsMapper inbillGoodsMapper;

    @Autowired
    private InBillMapper    inBillMapper;

    @Autowired
    private InbillDetailMapper inbillDetailMapper;

    @Autowired
    private InbillGoodsService inbillGoodsService;

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Autowired
    private PartsMapper partsMapper;

    @Value("${zebra.print.remote}")
    private String zebraPrintWebUrl;

    /**
     * 查询入库单货物
     *
     * @param id 入库单货物主键
     * @return 入库单货物
     */
    public InbillGoods selectInbillGoodsById(String id) {
        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return inbillGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询入库单货物
     *
     * @param ids 入库单货物 IDs
     * @return 入库单货物
     */
    public List<InbillGoods> selectInbillGoodsByIds(String[] ids) {
        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return inbillGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询入库单货物列表
     *
     * @param inbillGoods 入库单货物
     * @return 入库单货物集合
     */
    public List<InbillGoods> selectInbillGoodsList(InbillGoods inbillGoods) {
        QueryWrapper<InbillGoods> queryWrapper = getQueryWrapper(inbillGoods);
        return inbillGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增入库单货物
     *
     * @param inbillGoods 入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillGoods insertInbillGoods(InbillGoods inbillGoods) {
        inbillGoods.setId(IdUtil.simpleUUID());
        inbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        inbillGoodsMapper.insert(inbillGoods);
        return inbillGoods;
    }

    /**
     * 批量新增入库单货物
     *
     * @param inbillGoodsList 入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<InbillGoods> inbillGoodsList) {
        List<InbillGoods> collect = inbillGoodsList.stream().map(inbillGoods -> {
            inbillGoods.setId(IdUtil.simpleUUID());
            inbillGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            return inbillGoods;
        }).collect(Collectors.toList());
        return super.saveBatch(collect, collect.size());
    }

    /**
     * 修改入库单货物
     *
     * @param inbillGoods 入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillGoods updateInbillGoods(InbillGoods inbillGoods) {
        inbillGoodsMapper.updateById(inbillGoods);
        return inbillGoods;
    }

    /**
     * 批量修改入库单货物
     *
     * @param inbillGoodsList 入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateInbillGoodsList(List<InbillGoods> inbillGoodsList) {
        return super.updateBatchById(inbillGoodsList) ? 1 : 0;
    }

    /**
     * 批量删除入库单货物
     *
     * @param ids 需要删除的入库单货物主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInbillGoodsByIds(String[] ids) {
        List<InbillGoods> inbillGoodss = new ArrayList<>();
        for (String id : ids) {
            InbillGoods inbillGoods = new InbillGoods();
            inbillGoods.setId(id);
            inbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            inbillGoodss.add(inbillGoods);
        }
        return super.updateBatchById(inbillGoodss) ? 1 : 0;
    }

    /**
     * 删除入库单货物信息
     *
     * @param id 入库单货物主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInbillGoodsById(String id) {
        InbillGoods inbillGoods = new InbillGoods();
        inbillGoods.setId(id);
        inbillGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return inbillGoodsMapper.updateById(inbillGoods);
    }

    public QueryWrapper<InbillGoods> getQueryWrapper(InbillGoods inbillGoods) {
        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        if (inbillGoods != null) {
            //入库单号
            if (StrUtil.isNotEmpty(inbillGoods.getInBillCode())) {
                queryWrapper.eq("in_bill_code", inbillGoods.getInBillCode());
            }
            //入库详情主键
            if (StrUtil.isNotEmpty(inbillGoods.getInbillDetailId())) {
                queryWrapper.eq("inbill_detail_id", inbillGoods.getInbillDetailId());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(inbillGoods.getOnlyCode())) {
                queryWrapper.and(QueryWrapper -> {
                    QueryWrapper.like("only_code", inbillGoods.getOnlyCode()).or();
                    QueryWrapper.like("parts_code", inbillGoods.getOnlyCode());
                });
            }
            //机件号
            if (StrUtil.isNotEmpty(inbillGoods.getPartsCode())) {
                queryWrapper.like("parts_code", inbillGoods.getPartsCode());
            }
            //托盘编号
            if (StrUtil.isNotEmpty(inbillGoods.getTrayCode())) {
                queryWrapper.eq("tray_code", inbillGoods.getTrayCode());
            }
            //入库数量
            if (inbillGoods.getInBillNum() != null) {
                queryWrapper.eq("in_bill_num", inbillGoods.getInBillNum());
            }
            //生产日期
            if (inbillGoods.getProduceTime() != null) {
                queryWrapper.eq("produce_time", inbillGoods.getProduceTime());
            }
        }
        return queryWrapper;
    }

    /**
     * 根据唯一码查询入库单货物
     *
     * @param onlyCode 入库单货物唯一码
     * @return 入库单货物
     */
    public InbillGoods selectInbillGoodsByOnlyCode(String onlyCode) {
        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("only_code", onlyCode);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.last(" limit 1");
        return inbillGoodsMapper.selectOne(queryWrapper);
    }

    /**
     * 根据不同条件生成二维码数据
     *
     * @param inbillGoods
     * @return
     */
    public List getPrintData(InbillGoods inbillGoods) throws IOException, WriterException {
        List<InbillGoods> locationList = new ArrayList<>();
        //选择数据查询
        if (StringUtils.isNotNull(inbillGoods.getIds())) {
            locationList = super.listByIds(inbillGoods.getIds());
        } else {//过滤条件查询
            QueryWrapper<InbillGoods> queryWrapper = getQueryWrapper(inbillGoods);
            locationList = inbillGoodsMapper.selectList(queryWrapper);
        }
        //组装数据
        List<PrintDataDto> result = new ArrayList<>();
        PrintDataDto map = null;
        for (InbillGoods d : locationList) {
            map = new PrintDataDto();
            map.setCode(d.getOnlyCode());
            String text = "code=" + d.getOnlyCode() + ";" + "type=onlyCode";
            String filePath = "/qrcode/" + IdUtil.randomUUID() + ".png";
            CodeGeneratorUtil.generateQRCodeImage(text, 200, 200, RuoYiConfig.getProfile() + filePath);
            map.setUrl("/profile" + filePath);
            result.add(map);
        }

        return result;
    }
    /**
     * 修改入库单货物打印状态
     *
     * @param inbillGoods 入库单货物
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateStatus(InbillGoods inbillGoods) {
        List<InbillGoods> inbillGoodss = new ArrayList<>();
        if(StringUtils.isNotEmpty(inbillGoods.getIds())){
            for (String id : inbillGoods.getIds()) {
                InbillGoods obj = new InbillGoods();
                obj.setId(id);
                obj.setPrintStatus(InbillGoodsEnum.ALREADY.getCode());
                inbillGoodss.add(obj);
            }
        }
        return super.updateBatchById(inbillGoodss)?"1":"0";
    }

    /**
     * 获取入库单列表
     * @return 入库单数据
     */
    public List<InBill> getInbillInfo(){
        return inBillMapper.selectList(
                new LambdaQueryWrapper<InBill>()
                        .select(InBill::getId, InBill::getInBillCode)
                        .in(InBill::getInBillStatus, InBillStatusEnum.TOW.getCode(),InBillStatusEnum.THREE.getCode(),InBillStatusEnum.FOUR.getCode())
                        .eq(InBill::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                        .orderByDesc(InBill::getCreateTime)
        );
    }

    /**
     * 根据入库单号获取入库详情
     * @Param inbilCode 入库单号
     * @return 入库单数据
     */
    public List<InbillDetail> getInbillDetail(String inbilCode){
        if(StrUtil.isBlank(inbilCode)){
            throw new RuntimeException("参数不全");
        }
        LambdaQueryWrapper<InbillDetail> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(InbillDetail::getId, InbillDetail::getGoodsName)
                .eq(InbillDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InbillDetail::getInBillCode, inbilCode);
        return inbillDetailMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据入库单详情id获取获取数量
     * @Param inbilDetailId 入库单详情id
     * @return 入库货物数量
     */
    public Long getInBillNum(String inbilDetailId){
        if(StrUtil.isBlank(inbilDetailId)){
            throw new RuntimeException("参数不全");
        }
        LambdaQueryWrapper<InbillGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InbillGoods::getInbillDetailId, inbilDetailId)
                .isNull(InbillGoods::getPartsCode);
        return inbillGoodsMapper.selectCount(lambdaQueryWrapper);
    }

    /**
     * pda-保存机件号
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillGoodsDto saveInbillGoods(InbillGoodsDto inbillGoodsDto){
        //入库单号
        String inbillCode = inbillGoodsDto.getInBillCode();
        //入库详情id
        String inbillDetailId = inbillGoodsDto.getInBillDetailId();
        //机件号集合
        List<InbillGoods> inbillGoods = inbillGoodsDto.getInBillGoods();
        if(StrUtil.isBlank(inbillCode)){
            throw  new RuntimeException("入库单号不能为空！");
        }
        if(StrUtil.isBlank(inbillDetailId)){
            throw  new RuntimeException("货物不能为空！");
        }
        if(CollUtil.isEmpty(inbillGoods)){
            throw  new RuntimeException("机件号不能为空！");
        }
        List<String> partsCodeList = inbillGoods.stream().map(InbillGoods::getPartsCode).collect(Collectors.toList());
        boolean hasDuplicate = partsCodeList.stream().distinct().count() != partsCodeList.size();
        if(hasDuplicate){
            throw new RuntimeException("机件号存在重复，请检查输入！");
        }


        //根据入库详情id查询 判断机件号数量是否超过入库数量
        LambdaQueryWrapper<InbillGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(InbillGoods::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InbillGoods::getInbillDetailId,inbillDetailId)
                .isNull(InbillGoods::getPartsCode)
                .orderByAsc(InbillGoods::getOnlyCode);
        List<InbillGoods> inbillGoodsList = inbillGoodsMapper.selectList(lambdaQueryWrapper);
        if( inbillGoodsList.size() < partsCodeList.size()){
            throw new ServiceException("当前货物机件号数量不可超过:"+partsCodeList.size()+"个！");
        }
        //获取对应获取机件号id
        List<InbillGoods> inbillGoodsParts = inbillGoodsList.subList(0, partsCodeList.size());
        List<String> partsIds = inbillGoodsParts.stream().map(InbillGoods::getId).collect(Collectors.toList());

        //获取入库单详情
        LambdaQueryWrapper<InbillDetail> lambdaQueryinbillDetail = Wrappers.lambdaQuery();
        lambdaQueryinbillDetail .eq(InbillDetail::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(InbillDetail::getId,inbillDetailId);
        InbillDetail inbillDetail = inbillDetailMapper.selectOne(lambdaQueryinbillDetail);

        //获取货物信息
        LambdaQueryWrapper<GoodsInfo> queryWrapper1 = Wrappers.lambdaQuery();
        queryWrapper1.eq(GoodsInfo::getEnableStatus, GoodsInfoEnum.ENABLE.getCode())
                .eq(GoodsInfo::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                .eq(GoodsInfo::getGoodsCode, inbillDetail.getGoodsCode());
        GoodsInfo goodsInfo = goodsInfoMapper.selectOne(queryWrapper1);

        //获取货物信息
        QueryWrapper<InbillGoods> queryWrapperInbillGoods = new QueryWrapper<>();
        queryWrapperInbillGoods.eq("out_status", "0");
        queryWrapperInbillGoods.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapperInbillGoods.eq("good_code", goodsInfo.getGoodsCode());
        queryWrapperInbillGoods.in("parts_code", partsCodeList);

        List<InbillGoods> inbillGoods2 = inbillGoodsMapper.selectList(queryWrapperInbillGoods);
        List<String> partsCodes = inbillGoods2.stream().map(InbillGoods::getPartsCode).collect(Collectors.toList());
        for (InbillGoods inbillGood : inbillGoods) {
            if (CollUtil.isNotEmpty(partsCodes) && partsCodes.contains(inbillGood.getPartsCode())) {
                throw new RuntimeException("机件号已存在，请检查输入！");
            }
        }


        List<InbillGoods> dataList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // 遍历 partsId 列表
        for (int i = 0; i < partsIds.size(); i++) {
            // 获取当前下标的 id
            String partsId = partsIds.get(i);
            // 获取当前下标的机件号
            InbillGoods goods = inbillGoods.get(i);
            InbillGoods inbillGoods1 = new InbillGoods();
            inbillGoods1.setId(partsId);
            inbillGoods1.setPartsCode(goods.getPartsCode());
            //生成日期
            inbillGoods1.setProduceTime(goods.getProduceTime());
            //有效期
            cal.setTime(goods.getProduceTime());
            cal.add(Calendar.MONTH, goodsInfo.getWarranty());
            inbillGoods1.setPeriodValidity(cal.getTime());
            dataList.add(inbillGoods1);
        }
        inbillGoodsDto.setInBillGoods(dataList);
        if(CollUtil.isNotEmpty(dataList)){
            inbillGoodsService.updateBatchById(dataList);
        }
        return inbillGoodsDto;
    }


    /**
     * pda-根据条件去查询列表
     * @param inbillGoods 条件
     * @return 数据
     */
    public List<InbillGoods> getInbillGoodsInfo(InbillGoods inbillGoods){

        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        if (inbillGoods != null) {
            //机件号or入库单号
            if(StrUtil.isNotEmpty(inbillGoods.getInbillGoodsCode())){
                queryWrapper.and(i -> i.like("b.goods_name", inbillGoods.getInbillGoodsCode())
                        .or()
                        .like("a.parts_code", inbillGoods.getInbillGoodsCode()));
            }
            if(StrUtil.isNotEmpty(inbillGoods.getPrintStatus())){
                queryWrapper.eq("a.print_status", inbillGoods.getPrintStatus());
            }
        }
        queryWrapper.isNotNull("a.parts_code");
        queryWrapper.eq("a.del_flag", DelFlagEnum.DEL_NO.getCode());
        return inbillGoodsMapper.selectListInbillGoods(queryWrapper);
    }


    /**
     * 打印机件号
     * @return
     */
    public AjaxResult printGoods(Parts parts) throws Exception {
        List<Parts> trayList = new ArrayList<>();
        List<Parts> listParts = new ArrayList<>();
        if (StringUtils.isNotEmpty(parts.getIds())) {
            listParts = partsMapper.listByIds(parts.getIds());
        }else if(StrUtil.isNotEmpty(parts.getPartCodes())){
            String[] partCodesArray = parts.getPartCodes().split(",");
            listParts = partsMapper.listByPartCodes(partCodesArray);
        } else {
            QueryWrapper<Parts> queryWrapper = getQueryWrappers(parts);
            listParts = partsMapper.findPartsList(queryWrapper);
        }
        for (Parts p : listParts) {
            if (StringUtils.isEmpty(p.getPartsCode())) {
                throw new RuntimeException("机件号不能为空！");
            }
        }
        //选择数据查询
        if (StringUtils.isNotNull(parts.getIds())) {
            trayList = partsMapper.listByIds(parts.getIds());
        }else if (StringUtils.isNotNull(parts.getPartCodes())) {
            String[] partCodesArray = parts.getPartCodes().split(",");
            trayList = partsMapper.listByPartCodes(partCodesArray);
        } else {//过滤条件查询
            QueryWrapper<Parts> queryWrapper = getQueryWrappers(parts);
            trayList = partsMapper.findPartsList(queryWrapper);
        }
        //组装数据
        List<PartsPrintDto> result = new ArrayList<>();
        PartsPrintDto map = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        for (Parts d : trayList) {
            map = new PartsPrintDto();
            String produceTime = d.getProduceTime();
            String periodValidity =  d.getPeriodValidity();
            map.setId(d.getId());
            map.setCode(d.getPartsCode());
            map.setGoodsCode(d.getGoodsCode());
            map.setGoodsName(d.getGoodsName());
            map.setCharg(d.getCharg());
            map.setModel(d.getModel());
            map.setJldw(d.getJldw());
            map.setGys(d.getGys());
            map.setOnlyCode(d.getOnlyCode());
            map.setProduceTime(produceTime);
            map.setPeriodValidity(periodValidity);
            map.setPartsCode(d.getPartsCode());
            map.setOnlyCode(d.getOnlyCode());
            //生产日期
            Date parseProduceTime = sdf.parse(produceTime);
            calendar.setTime(parseProduceTime);
            int yearProduceTime = calendar.get(Calendar.YEAR);
            int monthProduceTime = calendar.get(Calendar.MONTH) + 1;  // 月份从0开始，需要加1
            int dayProduceTime = calendar.get(Calendar.DAY_OF_MONTH);

            //截止日期
            Date parsePeriodValidity = sdf.parse(periodValidity);
            calendar.setTime(parsePeriodValidity);
            int yearPeriodValidity = calendar.get(Calendar.YEAR);
            int monthPeriodValidity = calendar.get(Calendar.MONTH) + 1;  // 月份从0开始，需要加1
            int dayPeriodValidity = calendar.get(Calendar.DAY_OF_MONTH);
            //计算相差年数、月数
            LocalDate startDate = LocalDate.of(yearProduceTime, monthProduceTime, dayProduceTime);
            LocalDate endDate = LocalDate.of(yearPeriodValidity, monthPeriodValidity, dayPeriodValidity);
            Period period = Period.between(startDate, endDate);
            int years = period.getYears();
            int months = period.getMonths();
            if(months > 0){
                map.setStorageDate(years+"年"+months+"个月");
            }else{
                map.setStorageDate(years+"年");
            }
            String text = "code=" + d.getPartsCode() + ";onlyCode=" + d.getOnlyCode() + ";type=parts;goodsCode=" + d.getGoodsCode() + ";";
            String filePath = "/qrcode/" + IdUtil.randomUUID() + ".png";
            try {
                CodeGeneratorUtil.generateQRCodeImage(text, 100, 100, RuoYiConfig.getProfile() + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.setUrl("/profile" + filePath);
            result.add(map);
        }
        log.info("/warehousing/parts/printDataList");
        ObjectMapper objectMapper = new ObjectMapper();
        HttpUtils.sendPost(zebraPrintWebUrl+"/printParts",objectMapper.writeValueAsString(result));
        //修改打印状态
        partsMapper.updatePartsPrint(parts.getIds());
        return AjaxResult.success("success");
    }

    public QueryWrapper<Parts> getQueryWrappers(Parts parts) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        if (parts != null) {
            parts.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("d.del_flag", parts.getDelFlag());
            queryWrapper.in("b.in_bill_status", InBillStatusEnum.TOW.getCode(), InBillStatusEnum.THREE.getCode(), InBillStatusEnum.FOUR.getCode());
            queryWrapper.isNotNull("g.only_code");
            //入库单号
            if (StrUtil.isNotEmpty(parts.getInBillCode())) {
                queryWrapper.eq("d.in_bill_code", parts.getInBillCode());
            }
            //货物编码
            if (StrUtil.isNotEmpty(parts.getGoodsCode())) {
                queryWrapper.eq("d.goods_code", parts.getGoodsCode());
            }
            if (StringUtils.isNotEmpty(parts.getGoodsCodeList())) {
                queryWrapper.in("d.goods_code", parts.getGoodsCodeList());
            }
            //类别编码
            if (StrUtil.isNotEmpty(parts.getCategoryCode())) {
                queryWrapper.eq("d.category_code", parts.getCategoryCode());
            }
            //机件号
            if (StrUtil.isNotEmpty(parts.getPartsCode())) {
                queryWrapper.like("g.parts_code", parts.getPartsCode());
            }
            //唯一码
            if (StrUtil.isNotEmpty(parts.getOnlyCode())) {
                queryWrapper.like("g.only_code", parts.getOnlyCode());
            }
            //打印状态
            if (StrUtil.isNotEmpty(parts.getPrintStatus())) {
                queryWrapper.eq("g.print_status", parts.getPrintStatus());
            }
            //生产日期
            if (parts.getProduceTime() != null) {
                queryWrapper.eq("produce_time", parts.getProduceTime());
            }
            //有效期
            if (parts.getPeriodValidity() != null) {
                queryWrapper.eq("period_validity", parts.getPeriodValidity());
            }
        }
        return queryWrapper;
    }

    /**
     * pda - 根据id机件号信息
     * @param id 数据id
     * @return 数据
     */
    public InbillGoods getGoodsById(String id){
        QueryWrapper<InbillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.id",id);
        return  inbillGoodsMapper.selectByIdGoods(queryWrapper);
    }


    /**
     * pda - 修改机件号
     * @param inbillGoods 参数
     * @return 数据
     */
    @Transactional(rollbackFor = Exception.class)
    public InbillGoods updateGoods(InbillGoods inbillGoods) {
        if(StrUtil.isNotEmpty(inbillGoods.getProduceTime().toString())){
            //获取货物信息
            LambdaQueryWrapper<GoodsInfo> queryWrapper1 = Wrappers.lambdaQuery();
            queryWrapper1.eq(GoodsInfo::getEnableStatus, GoodsInfoEnum.ENABLE.getCode())
                    .eq(GoodsInfo::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .eq(GoodsInfo::getGoodsCode, inbillGoods.getGoodsCode());
            GoodsInfo goodsInfo = goodsInfoMapper.selectOne(queryWrapper1);
            Calendar cal = Calendar.getInstance();
            //有效期
            cal.setTime(inbillGoods.getProduceTime());
            cal.add(Calendar.MONTH, goodsInfo.getWarranty());
            inbillGoods.setPeriodValidity(cal.getTime());
        }
        inbillGoodsMapper.updateById(inbillGoods);
        return inbillGoods;
    }

}
