package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.InBillStatusEnum;
import com.ruoyi.common.enums.PrintStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CodeGeneratorUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.wms.basics.domain.GoodsInfo;
import com.ruoyi.wms.basics.mapper.GoodsInfoMapper;
import com.ruoyi.wms.enums.GoodsInfoEnum;
import com.ruoyi.wms.enums.OutStatusEnum;
import com.ruoyi.wms.enums.PartsEnum;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import com.ruoyi.wms.warehousing.dto.PartsPrintDto;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.warehousing.mapper.PartsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机件号记录Service接口
 *
 * @author nf
 * @date 2023-02-14
 */
@Slf4j
@Service
public class PartsService extends ServiceImpl<PartsMapper, Parts> {

    @Autowired(required = false)
    private PartsMapper partsMapper;
    @Autowired
    protected Validator validator;

    @Autowired(required = false)
    protected InbillGoodsMapper inbillGoodsMapper;
    @Autowired(required = false)
    protected InbillGoodsService inbillGoodsService;
    @Autowired(required = false)
    protected GoodsInfoMapper goodsInfoMapper;
    @Autowired(required = false)
    protected PartsService partsService;

    /**
     * 查询机件号记录
     *
     * @param id 机件号记录主键
     * @return 机件号记录
     */
    public Parts selectPartsById(String id) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return partsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询机件号记录
     *
     * @param ids 机件号记录 IDs
     * @return 机件号记录
     */
    public List<Parts> selectPartsByIds(String[] ids) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return partsMapper.selectList(queryWrapper);
    }

    /**
     * 查询机件号记录列表
     *
     * @param parts 机件号记录
     * @return 机件号记录集合
     */
    public List<Parts> selectPartsList(Parts parts) {
        QueryWrapper<Parts> queryWrapper = getQueryWrapper(parts);
        return partsMapper.select(queryWrapper);
    }

    /**
     * 新增机件号记录
     *
     * @param parts 机件号记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Parts insertParts(Parts parts) {
        parts.setId(IdUtil.simpleUUID());
        parts.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        parts.setPrintStatus(PartsEnum.NOT.getCode());
        partsMapper.insert(parts);
        return parts;
    }

    /**
     * 修改机件号记录
     *
     * @param partsList 机件号记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateParts(List<InbillGoods> partsList) throws ParseException {
        if (partsList.size() <= 0) {
            throw new RuntimeException("机件号数据不能为空！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        List<String> copyPartsCode = new ArrayList<>();
        List<String> onlyCodes = new ArrayList<>();
        //判断机件号是否有重复
        for (InbillGoods parts : partsList) {
            if (StringUtils.isEmpty(parts.getPartsCode())) {
                throw new RuntimeException("机件号不能为空！");
            } else {
                //校验机件号长度不能为超过30字符
                if (parts.getPartsCode().length() > 30) {
                    throw new RuntimeException("机件号长度不能超过30个字符！");
                }
            }
            if (StringUtils.isEmpty(parts.getProduceDate())) {
                throw new RuntimeException("生产日期不能为空！");
            }
        }
        //根据货物编码分组
        Map<String, List<InbillGoods>> groupedMap = partsList.stream() .collect(Collectors.groupingBy(InbillGoods::getGoodsCode));
        //循环
        groupedMap.forEach((key, value) -> {
            if(value.size()>1){
                // 检查该分组中每个 InbillGoods 对象的机件号是否相等
                // 检查该分组中每个 InbillGoods 对象的机件号是否相等
                boolean allMechanismNumbersEqual = value.stream()
                        .map(InbillGoods::getPartsCode)
                        .distinct() // 去重
                        .count() != value.size(); // 如果去重后的数量小于原来的数量，则表示有重复机件号
                if(allMechanismNumbersEqual){
                    throw new RuntimeException(key+"机件号存在重复，请检查输入！");
                }
            }
            // 校验机件号重复
            List<String> partsCodes = partsMapper.checkMultiCode(value,key);
            if (CollUtil.isNotEmpty(partsCodes) && partsCodes.contains(value)) {
                throw new RuntimeException("机件号已存在，请检查输入！");
            }
            for (InbillGoods inbillGoods : value) {
                inbillGoods.setOutStatus(OutStatusEnum.NOT_OUT.getCode());
                inbillGoods.setPrintStatus(PrintStatusEnum.PRINT_NO.getCode());
                copyPartsCode.add(inbillGoods.getPartsCode());
                onlyCodes.add(inbillGoods.getOnlyCode());
            }
        });
        List<InbillGoods> result = inbillGoodsMapper.selectByOnlyCodes(onlyCodes);
        LambdaQueryWrapper<GoodsInfo> queryWrapper1 = Wrappers.lambdaQuery();
        queryWrapper1.eq(GoodsInfo::getEnableStatus, GoodsInfoEnum.ENABLE.getCode())
                .eq(GoodsInfo::getDelFlag, DelFlagEnum.DEL_NO.getCode());
        List<GoodsInfo> goodsInfos = goodsInfoMapper.selectList(queryWrapper1);
        for (InbillGoods parts : partsList) {
            parts.setProduceTime(sdf.parse(parts.getProduceDate()));
            for (InbillGoods part1 : result) {
                if (parts.getOnlyCode().equals(part1.getOnlyCode())) {
                    parts.setId(part1.getId());
                    parts.setGoodsCode(part1.getGoodsCode());
                }
            }
            for (GoodsInfo goodsInfo : goodsInfos) { ////
                if (parts.getGoodsCode().equals(goodsInfo.getGoodsCode())) {
                    cal.setTime(sdf.parse(parts.getProduceDate()));
                    cal.add(Calendar.MONTH, goodsInfo.getWarranty());
                    parts.setPeriodValidity(cal.getTime());
                }
            }
        }
        if (partsList.size() > 0) {
            inbillGoodsService.updateBatchById(partsList);
        }

        return "保存成功";
    }

    /**
     * 批量删除机件号记录
     *
     * @param ids 需要删除的机件号记录主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deletePartsByIds(String[] ids) {
        List<Parts> partss = new ArrayList<>();
        for (String id : ids) {
            Parts parts = new Parts();
            parts.setId(id);
            parts.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            partss.add(parts);
        }
        return super.updateBatchById(partss) ? 1 : 0;
    }

    /**
     * 删除机件号记录信息
     *
     * @param id 机件号记录主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deletePartsById(String id) {
        Parts parts = new Parts();
        parts.setId(id);
        parts.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return partsMapper.updateById(parts);
    }

    public QueryWrapper<Parts> getQueryWrapper(Parts parts) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        if (parts != null) {
            parts.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", parts.getDelFlag());
            //机件号
            if (StrUtil.isNotEmpty(parts.getPartsCode())) {
                queryWrapper.like("parts_code", parts.getPartsCode());
            }
            //生产日期
            if (parts.getProduceTime() != null) {
                queryWrapper.eq("produce_time", parts.getProduceTime());
            }
            //有效期
            if (parts.getPeriodValidity() != null) {
                queryWrapper.eq("period_validity", parts.getPeriodValidity());
            }
            //打印状态
            if (StrUtil.isNotEmpty(parts.getPrintStatus())) {
                queryWrapper.eq("print_status", parts.getPrintStatus());
            }
        }
        return queryWrapper;
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

    public QueryWrapper<Parts> getAddQueryWrappers(Parts parts) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        if (parts != null) {
            parts.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("d.del_flag", parts.getDelFlag());
            queryWrapper.eq("b.in_bill_status", InBillStatusEnum.TOW.getCode());
            queryWrapper.isNull("g.parts_code");
            queryWrapper.eq("g.parts_code", "");
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

    public QueryWrapper<Parts> getQueryGoodsWrappers(List<String> goodsCodeList) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        if (goodsCodeList != null) {
            queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
            //货物编码
            queryWrapper.in("goods_code", goodsCodeList);
        }
        return queryWrapper;
    }

    public QueryWrapper<Parts> getQueryPartsWrappers(Parts parts) {
        QueryWrapper<Parts> queryWrapper = new QueryWrapper<>();
        parts.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        queryWrapper.eq("del_flag", parts.getDelFlag());
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param partsList     模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName      操作人姓名
     * @return
     */
    public String importData(List<Parts> partsList, boolean updateSupport, String operName) throws ParseException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("task1");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Parts> oldData = new ArrayList<>();
        List<Map<String, String>> improtData = new ArrayList<>();
        List<Map<String, String>> vailData = new ArrayList<>();
        List<String> inbillCodes = new ArrayList<>();
        List<String> goodsCodeList = new ArrayList<>();
        List<String> onlyCodes = new ArrayList<>();
        List<InbillGoods> partss = new ArrayList<>();

        try {
            for (Parts parts : partsList) {
                if (StringUtils.isEmpty(parts.getPartsCode())) {
                    return "机件号不能为空!";
                }
                if (StringUtils.isEmpty(parts.getGoodsCode())) {
                    return "货物编码不能为空!";
                }
                if (StringUtils.isEmpty(parts.getOnlyCode())) {
                    return "唯一码不能为空!";
                }
                //校验机件号长度不能为超过30字符
                if (StringUtils.isNotEmpty(parts.getPartsCode())) {
                    if (parts.getPartsCode().length() > 30) {
                        return "机件号长度不能超过30个字符！";
                    }
                }
                if (StringUtils.isEmpty(parts.getProduceTime())) {
                    return "生产日期不能为空!";
                }
                try {
                    sdf.parse(parts.getProduceTime());
                } catch (Exception e) {
                    return "请输入正确格式的生产日期！！";
                }
                if (StringUtils.isEmpty(parts.getInBillCode())) {
                    throw new ServiceException("导入模板中的入库单号不能为空");
                }
                goodsCodeList.add(parts.getGoodsCode());
                if (onlyCodes.contains(parts.getOnlyCode())) {
                    throw new RuntimeException("导入模板数据中第" + onlyCodes.size() + "行的唯一码有重复数据。");
                } else {
                    onlyCodes.add(parts.getOnlyCode());
                }
                Map<String, String> m = new HashMap<>();
                m.put("goodsCode", parts.getGoodsCode());
                m.put("onlyCode", parts.getOnlyCode());
                improtData.add(m);
                if (!inbillCodes.contains(parts.getInBillCode())) {
                    inbillCodes.add(parts.getInBillCode());
                }
            }
            if (inbillCodes.size() > 1) {
                throw new RuntimeException("导入模板中数据必须是同一入库单号下的数据！");
            }
            String inbillCode = partsList.get(0).getInBillCode();
            oldData = partsMapper.selectByInbillCode(inbillCode);
            vailData = oldData.stream().map(parts -> {
                Map<String, String> m = new HashMap<>();
                m.put("goodsCode", parts.getGoodsCode());
                m.put("onlyCode", parts.getOnlyCode());
                return m;
            }).collect(Collectors.toList());
            for (int i = 0; i < improtData.size(); i++) {
                Map<String, String> m = improtData.get(i);
                if (!vailData.contains(m)) {
                    throw new RuntimeException("导入模板数据的第" + (i + 2) + "行数据的唯一码和货物编码与原纪录不一致。");
                }
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("导入模板错误！");
        }
        if (StringUtils.isNull(partsList) || partsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        partss = inbillGoodsMapper.selectByOnlyCodes(onlyCodes);
        if (onlyCodes.size() != partss.size()) {
            throw new ServiceException("导入数据的唯一码错误或被更改！");
        }

        // 查询货物编号、有效期
        QueryWrapper<Parts> queryWrapper = getQueryGoodsWrappers(goodsCodeList);
        List<Parts> list = partsMapper.findPeriodByCode(queryWrapper);
        int successNum = 0;
        int failureNum = 1;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        List<InbillGoods> inbillGoods = new ArrayList<>();
        for (Parts parts : partsList) {

            InbillGoods inbillGoods1 = new InbillGoods();
            inbillGoods1.setGoodsCode(parts.getGoodsCode());
            inbillGoods1.setInBillCode(parts.getInBillCode());
            inbillGoods1.setPartsCode(parts.getPartsCode());
            inbillGoods1.setOnlyCode(parts.getOnlyCode());
            if (null != parts.getProduceTime()) {
                inbillGoods1.setProduceTime(sdf.parse(parts.getProduceTime()));
            }
            // 计算有效期数据
            for (Parts p : list) {
                if (parts.getGoodsCode().equals(p.getGoodsCode())) {
                    cal.setTime(sdf.parse(parts.getProduceTime()));
                    cal.add(Calendar.MONTH, Integer.parseInt(p.getWarranty()));
                    parts.setPeriodValidity(sdf.format(cal.getTime()));
                    inbillGoods1.setPeriodValidity(cal.getTime());
                }
            }
            endFor:
            for (InbillGoods p : partss) {
                if (parts.getOnlyCode().equals(p.getOnlyCode())) {
                    parts.setId(p.getId());
                    inbillGoods1.setId(p.getId());
                    break endFor;
                }
            }
            inbillGoods.add(inbillGoods1);
        }
        //todo 验证唯一码
        List<String> onlyMultiCodes = partsMapper.checkOnlyMultiCode(inbillGoods);
        // 根据货物编码分组
        Map<String, List<InbillGoods>> groupedMap = inbillGoods.stream().collect(Collectors.groupingBy(InbillGoods::getGoodsCode, TreeMap::new, Collectors.toList()));
        int index = 1;
        try {
            // 循环
            for (Map.Entry<String, List<InbillGoods>> entry : groupedMap.entrySet()) {
                String key = entry.getKey();
                List<InbillGoods> value = entry.getValue();
                // 校验机件号重复
                List<String> partsCodes = partsMapper.checkMultiCode(value,key);
                if (value.size() > 1) {
                    // 检查该分组中每个 InbillGoods 对象的机件号是否相等
                    boolean allMechanismNumbersEqual = value.stream()
                            .map(InbillGoods::getPartsCode)
                            .distinct() // 去重
                            .count() != value.size(); // 如果去重后的数量小于原来的数量，则表示有重复机件号
                    if (allMechanismNumbersEqual) {
                        Set<String> processedPartsCodes = new HashSet<>();
                        for (InbillGoods inbill : value) {
                            index++;
                            String partsCode = inbill.getPartsCode();
                            if (processedPartsCodes.contains(partsCode)) {
                                // 判断机件号是否重复
                                failureNum++;
                                failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，机件号重复；");
                            } else {
                                processedPartsCodes.add(partsCode);
                                successNum++;
                            }
                            // 验证唯一码是否存在
                            if ((CollUtil.isNotEmpty(onlyMultiCodes) && !onlyMultiCodes.contains(inbill.getOnlyCode())) || CollUtil.isEmpty(onlyMultiCodes)) {
                                if (null != inbill.getProduceTime()) {
                                    inbill.setProduceTime(null);
                                }
                                failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，该条数据唯一码不存在；");
                            }

                        }
                    } else if (CollUtil.isNotEmpty(partsCodes)) {
                        Set<String> processedPartsCodes1 = new HashSet<>();
                        for (InbillGoods inbill : value) {
                            String partsCode = inbill.getPartsCode();
                            if (processedPartsCodes1.contains(partsCode) || partsCodes.contains(partsCode)) {
                                // 判断机件号是否重复
                                failureNum++;
                                failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，机件号已存在；");
                            } else {
                                processedPartsCodes1.add(partsCode);
                                successNum++;
                            }
                            index++;
                        }
                    } else {
                        // 机件号不重复，直接处理成功的情况
                        successNum += value.size(); // 记录成功数量
                        for (InbillGoods goods : value) {
                            // 验证唯一码是否存在
                            if ((CollUtil.isNotEmpty(onlyMultiCodes) && !onlyMultiCodes.contains(goods.getOnlyCode())) || CollUtil.isEmpty(onlyMultiCodes)) {
                                if (null != goods.getProduceTime()) {
                                    goods.setProduceTime(null);
                                }
                                failureNum++;
                                failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，该条数据唯一码不存在；");
                            }
                            index++;
                        }
                    }
                } else {
                    for (InbillGoods goods : value) {
                        // 验证唯一码是否存在
                        if ((CollUtil.isNotEmpty(onlyMultiCodes) && !onlyMultiCodes.contains(goods.getOnlyCode())) || CollUtil.isEmpty(onlyMultiCodes)) {
                            if (null != goods.getProduceTime()) {
                                goods.setProduceTime(null);
                            }
                            failureNum++;
                            failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，该条数据唯一码不存在；");
                        } else {
                            successNum++; // 记录成功数量
                        }
                        index++;
                    }
                }
            }

        } catch (Exception e) {
            index++; // 新增一个变量来保存行号
            String msg = "<br/>" + index + "、第" + index + "行数据导入失败：";
            failureMsg.append(msg + e.getMessage());
            log.error(msg, e);
        }
        // 处理最终结果
        if (failureNum > 1) {
            failureNum--;
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            inbillGoodsService.updateBatchById(inbillGoods);
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
        }
        stopWatch.stop();
        stopWatch.prettyPrint();
        return successMsg.toString();




//        //todo 验证
//        List<String> codes = partsMapper.checkMultiCode(inbillGoods);
//        //todo 验证唯一码
//        List<String> onlyMultiCodes = partsMapper.checkOnlyMultiCode(inbillGoods);
//        int index = 1;
//        try {
//            for (InbillGoods goods : inbillGoods) {
//                if (CollUtil.isNotEmpty(codes) && codes.contains(goods.getPartsCode())) {
//                    if (null != goods.getProduceTime()) {
//                        goods.setProduceTime(null);
//                    }
//                    //判断是否重复
//                    failureNum++;
//                    failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，机件号已存在；");
//                } else if ((CollUtil.isNotEmpty(onlyMultiCodes) &&
//                        !onlyMultiCodes.contains(goods.getOnlyCode())) || CollUtil.isEmpty(onlyMultiCodes)) {
//                    if (null != goods.getProduceTime()) {
//                        goods.setProduceTime(null);
//                    }
//                    failureNum++;
//                    failureMsg.append("<br/>" + index + "、第" + index + "行数据导入失败，该条数据唯一码不存在；");
//                } else {
//                    successNum++;
//                }
//                index++;
//            }
//
//        } catch (Exception e) {
//            index++;
//            String msg = "<br/>" + index + "、第" + index + "行数据导入失败：";
//            failureMsg.append(msg + e.getMessage());
//            log.error(msg, e);
//        }
//        if (failureNum > 1) {
//            failureNum--;
//            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
//            throw new ServiceException(failureMsg.toString());
//        } else {
//            inbillGoodsService.updateBatchById(inbillGoods);
//            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
//        }
//        stopWatch.stop();
//        stopWatch.prettyPrint();
//        return successMsg.toString();
    }


        /**
         * 组装打印数据
         *
         * @param parts
         * @return
         */
    public AjaxResult getPrintData(Parts parts) throws ParseException {
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
        if ("0".equals(parts.getType())) {//打印
            for (int i = 0; i < trayList.size(); i++) {
                if (trayList.get(i).getPrintStatus().equals(PartsEnum.ALREADY.getCode())) {
                    throw new RuntimeException("打印功能不可包含已打印状态的数据");
                }
            }
        } else {//补打
            for (int i = 0; i < trayList.size(); i++) {
                if (trayList.get(i).getPrintStatus().equals(PartsEnum.NOT.getCode())) {
                    throw new RuntimeException("补打功能不可包含未打印状态的数据");
                }
            }
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
        return AjaxResult.success(result);
    }

    /**
     * IO控制查询机件号
     *
     * @param parts
     * @return
     */
    public AjaxResult getIoPrintData(Parts parts) {
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
        for (Parts d : trayList) {
            map = new PartsPrintDto();
            map.setId(d.getId());
            map.setCode(d.getPartsCode());
            map.setGoodsCode(d.getGoodsCode());
            map.setGoodsName(d.getGoodsName());
            map.setCharg(d.getCharg());
            map.setModel(d.getModel());
            map.setJldw(d.getJldw());
            map.setGys(d.getGys());
            map.setOnlyCode(d.getOnlyCode());
            map.setProduceTime(d.getProduceTime());
            map.setPeriodValidity(d.getPeriodValidity());
            map.setPartsCode(d.getPartsCode());
            map.setOnlyCode(d.getOnlyCode());
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
        return AjaxResult.success(result);
    }

    /**
     * 查询机件号记录展示列表
     *
     * @param parts
     * @return
     */
    public List<Parts> findPartsList(Parts parts) {
        QueryWrapper<Parts> queryWrapper = getQueryWrappers(parts);
        return partsMapper.findPartsList(queryWrapper);
    }

    /**
     * 级联获取物品类型
     *
     * @param parts
     * @return
     */
    public List<Parts> getCategoryCode(Parts parts) {
        return partsMapper.getCategoryCode(parts);
    }

    /**
     * 初始化获取入库单号
     *
     * @return
     */
    public List<InbillDetail> findInbillCode() {
        return partsMapper.findInbillCode();
    }

    /**
     * 下载模板数据
     *
     * @param parts
     * @return
     */
    public List<Parts> importPartsList(Parts parts) {
        QueryWrapper<Parts> queryWrapper = getQueryWrappers(parts);
        return partsMapper.importPartsList(queryWrapper);
    }

    /**
     * 修改打印状态
     *
     * @param parts
     * @return
     */
    public String updatePartsPrint(Parts parts) {
        partsMapper.updatePartsPrint(parts.getIds());
        return "200";
    }

    /**
     * 新增初始化获取入库单号
     *
     * @return
     */
    public List<InbillDetail> findAddInbillCode() {
        return partsMapper.findAddInbillCode();
    }

    /**
     * 新增查询机件号记录展示列表
     *
     * @param parts
     * @return
     */
    public List<Parts> findAddPartsList(Parts parts) {
        return partsMapper.findAddPartsList(parts);
    }

}
