package com.ruoyi.wms.warehousing.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.wms.basics.domain.Temp;
import com.ruoyi.wms.basics.service.TempService;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.*;
import com.ruoyi.wms.utils.SerialCodeUtils;
import com.ruoyi.wms.warehousing.domain.InBill;
import com.ruoyi.wms.warehousing.domain.InbillDetail;
import com.ruoyi.wms.warehousing.domain.InbillGoods;
import com.ruoyi.wms.warehousing.domain.Parts;
import com.ruoyi.wms.warehousing.dto.InBillPrintDto;
import com.ruoyi.wms.warehousing.mapper.InBillMapper;
import com.ruoyi.wms.warehousing.mapper.InbillDetailMapper;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 入库单信息Service接口
 *
 * @author ruoyi
 * @date 2023-02-01
 */
@Slf4j
@Service
public class InBillService extends ServiceImpl<InBillMapper, InBill> {

    @Autowired(required = false)
    private InBillMapper inBillMapper;

    @Autowired(required = false)
    private InbillDetailMapper inbillDetailMapper;

    @Autowired
    private InbillDetailService inbillDetailService;

    @Autowired
    private WmsWarehouseCheckDetailService checkDetailService;

    @Autowired(required = false)
    private InbillGoodsMapper inbillGoodsMapper;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TempService tempService;

    @Autowired
    private SerialCodeUtils serialCodeUtils;

    /**
     * 查询入库单信息
     *
     * @param id 入库单信息主键
     * @return 入库单信息
     */
    public InBill selectInBillById(String id) {
        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        InBill inBill = inBillMapper.selectOne(queryWrapper);
        QueryWrapper<InbillDetail> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("del_flag",DelFlagEnum.DEL_NO)
                .eq("in_bill_code",inBill.getInBillCode());
        List<InbillDetail> details = inbillDetailService.list();
        inBill.setInbillDetailList(details);
        return inBill;
    }

    /**
     * 组装打印数据
     * @param inBill1
     * @return
     */
    public AjaxResult getPrintData(InBill inBill1) {

        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", inBill1.getId());
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        InBill inBill = inBillMapper.selectOne(queryWrapper);
        QueryWrapper<InbillDetail>  detailQueryWrapper= new QueryWrapper<>();
        detailQueryWrapper.eq("in_bill_code",inBill.getInBillCode());
        detailQueryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        List<InbillDetail> details = inbillDetailService.list(detailQueryWrapper);
        for(InbillDetail detail: details){
            detail.setVariance(detail.getReportNum().subtract(detail.getInBillNum()));
        }
        inBill.setInbillDetailList(details);

        //组装数据
        String img = IdUtil.randomUUID();
        String text = "code=" + inBill.getInBillCode() +";type=inbill";
        String filePath = "/qrcode/" + img + ".png";
        try {
            CodeGeneratorUtil.generateQRCodeImage(text, 100, 100, RuoYiConfig.getProfile() + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/profile" + filePath;
        inBill.setPrintCodeList(path);
        inBill.setImgCode(img + ".png");
        return AjaxResult.success(inBill);
    }


    /**
     * 根据ids查询入库单信息
     *
     * @param ids 入库单信息 IDs
     * @return 入库单信息
     */
    public List<InBill> selectInBillByIds(String[] ids) {
        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return inBillMapper.selectList(queryWrapper);
    }

    /**
     * 查询入库单信息列表
     *
     * @param inBill 入库单信息
     * @return 入库单信息集合
     */
    public List<InBill> selectInBillList(InBill inBill) {
        QueryWrapper<InBill> queryWrapper = getQueryWrapper(inBill);
        return inBillMapper.select(queryWrapper);
    }


    /**
     * 新增入库单信息
     *
     * @param inBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InBill insertInBill(InBill inBill) {
        inBill.setId(IdUtil.simpleUUID());
        inBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        inBill.setInBillStatus(InBillEnum.WAIT.getCode());
        inBill.setTakeTrayStatus(TakeTrayStatusEnum.NOT.getCode());
        inBillMapper.insert(inBill);
        return inBill;
    }

    /**
     * 修改入库单信息
     *
     * @param inBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InBill updateInBill(InBill inBill) {
        if(!canUpdate(inBill.getId())){
            throw new ServiceException("该入库单状态已更改，不可再进行数据维护");
        }
        //要删除的详情数据id
        if(CollectionUtil.isNotEmpty(inBill.getIds())){
            inbillDetailService.deleteInbillDetailByIds(inBill.getIds().toArray(new String[inBill.getIds().size()]));
        }
        BigDecimal weight = new BigDecimal("0.00");
        BigDecimal volume = new BigDecimal("0.00");
        String username = SecurityUtils.getUsername();//登录用户名
        List<InbillDetail> inbillDetailList = inBill.getInbillDetailList();

        List<InbillDetail> oldList = new ArrayList<>();
        List<InbillDetail> newList = new ArrayList<>();
        if (inbillDetailList != null) {
            //修改详情
            for (InbillDetail inbillDetail : inbillDetailList) {
                if(StringUtils.isEmpty(inbillDetail.getId())){
                    inbillDetail.setId(IdUtils.simpleUUID());
                    inbillDetail.setInBillCode(inBill.getInBillCode());
                    inbillDetail.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    inbillDetail.setCreateBy(username);
                    inbillDetail.setCreateTime(new Date());
                    newList.add(inbillDetail);
                }else{
                    inbillDetail.setUpdateBy(username);
                    inbillDetail.setUpdateTime(new Date());
                    oldList.add(inbillDetail);
                }
            }
            if(newList.size()>0){
                inbillDetailService.saveBatch(newList);
            }
            if(oldList.size()>0){
                inbillDetailService.updateBatchById(oldList);
            }
            LambdaQueryWrapper<InbillDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InbillDetail::getDelFlag,DelFlagEnum.DEL_NO.getCode())
                    .eq(InbillDetail::getInBillCode,inBill.getInBillCode());
            List<InbillDetail> dataList = inbillDetailMapper.selectList(queryWrapper);
            for(InbillDetail detail: dataList){
                //入库单货物重量累加
                weight = weight.add(detail.getReportNum().multiply(detail.getWeight()));
                //入库单体货物积累加
                volume = volume.add(detail.getReportNum().multiply(detail.getVolume()));
            }
            inBill.setVolume(volume);
            inBill.setWeight(weight);
        }
        //修改抬头
        inBillMapper.updateById(inBill);
        return inBill;
    }

    /**
     * 批量删除入库单信息
     *
     * @param ids 需要删除的入库单信息主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInBillByIds(String[] ids) {
        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        List<InBill> old = inBillMapper.selectList(queryWrapper);
        for(InBill inBill:old){
            if(!inBill.getInBillStatus().equals(InBillEnum.WAIT.getCode())){
                throw new ServiceException("入库单"+inBill.getInBillCode()+"的状态已更改，不可进行删除");
            }
        }
        List<InBill> inBills = new ArrayList<>();
        for (String id : ids) {
            InBill inBill = new InBill();
            inBill.setId(id);
            inBill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            inBills.add(inBill);
        }
        List<InBill> inBillList = selectInBillByIds(ids);
        for (InBill d:inBillList) {
            inbillDetailMapper.deleteNoExist(SecurityUtils.getUsername(),d.getInBillCode(),null);
        }
        return super.updateBatchById(inBills) ? 1 : 0;
    }

    /**
     * 删除入库单信息信息
     *
     * @param id 入库单信息主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInBillById(String id) {
        InBill inBill = new InBill();
        inBill.setId(id);
        inBill.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return inBillMapper.updateById(inBill);
    }

    public QueryWrapper<InBill> getQueryWrapper(InBill inBill) {
        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        if (inBill != null) {
            inBill.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",inBill.getDelFlag());
            //入库单号
            if (StrUtil.isNotEmpty(inBill.getInBillCode())) {
                queryWrapper.like("in_bill_code",inBill.getInBillCode());
            }
            //批次
            if (StrUtil.isNotEmpty(inBill.getCharg())) {
                queryWrapper.like("charg",inBill.getCharg());
            }
            //入库单状态;(1.待收货  2.已验货  3.上架中   4.已上架  5.已作废)
            if (StrUtil.isNotEmpty(inBill.getInBillStatus())) {
                if(inBill.getInBillStatus().indexOf("-")==-1){
                    queryWrapper.eq("in_bill_status",inBill.getInBillStatus());
                }else{
                    queryWrapper.ne("in_bill_status",inBill.getInBillStatus().split("-")[0]);
                }
            }
            //取盘状态（0-未取盘；1-已取盘；2取盘中）
            if (StrUtil.isNotEmpty(inBill.getTakeTrayStatus())) {
                queryWrapper.eq("take_tray_status",inBill.getTakeTrayStatus());
            }
            //入库类别;(1.期初入库 2.普通入库  3.盘盈入库 4.晾晒入库  5.其他入库)
            if (StrUtil.isNotEmpty(inBill.getInBillCategory())) {
                queryWrapper.eq("in_bill_category",inBill.getInBillCategory());
            }
            //根据文号
            if (StrUtil.isNotEmpty(inBill.getDocNo())) {
                queryWrapper.like("doc_no",inBill.getDocNo());
            }
            //入库流水号
            if (StrUtil.isNotEmpty(inBill.getInBillSerial())) {
                queryWrapper.like("in_bill_serial",inBill.getInBillSerial());
            }
            //库房流水号
            if (StrUtil.isNotEmpty(inBill.getStorageSerial())) {
                queryWrapper.like("storage_serial",inBill.getStorageSerial());
            }
        }
        return queryWrapper;
    }

    /**
     * 创建入库单信息
     *
     * @param inBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public InBill createInBill(@NotNull InBill inBill) {
        // if (checkDetailService.haveChecking()) {
        //     throw new ServiceException("存在未完结的盘点数据，创建入库单失败！");
        // }
        BigDecimal weight = new BigDecimal("0.00");
        BigDecimal volume = new BigDecimal("0.00");
        String inBillCode = serialCodeUtils.getOrderNo(InBillNoPrefixEnum.getPrefix(inBill.getInBillCategory()));//入库单号
        String charg = serialCodeUtils.getCharg();//批次
        List<InbillDetail> inbillDetailList = inBill.getInbillDetailList();
        //保存抬头
        inBill.setInBillCode(inBillCode);
        inBill.setCharg(charg);
        if(inbillDetailList.size()>0){
            //保存详情
            for (InbillDetail inbillDetail : inbillDetailList) {
                inbillDetail.setInBillCode(inBillCode);
                inbillDetail.setInBillNum(new BigDecimal("0.00"));
                //入库单货物重量累加
                weight = weight.add(inbillDetail.getReportNum().multiply(inbillDetail.getWeight()));
                //入库单货物体积累加
                volume = volume.add(inbillDetail.getReportNum().multiply(inbillDetail.getVolume()));
            }
            inbillDetailService.saveBatch(inbillDetailList);
        }
        inBill.setVolume(volume);
        inBill.setWeight(weight);
        insertInBill(inBill);
        return inBill;
    }

    /**
     * 删除入库单信息
     *
     * @param inBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteInBill(@NotNull InBill inBill) {

        String username = SecurityUtils.getUsername();
        inbillDetailMapper.deleteNoExist(username, inBill.getInBillCode(), null);
        return deleteInBillById(inBill.getId());
    }

    /**
     * 修改入库单状态
     *
     * @param inBill 入库单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(InBill inBill) {
        InBill old = inBillMapper.selectById(inBill.getId());
        if(InBillEnum.REPEAL.getCode().equals(inBill.getInBillStatus())){
            if(!old.getInBillStatus().equals(InBillEnum.INSPECTED.getCode())){
                throw new ServiceException("该入库单状态已更改，不可作废");
            }
            InBill oldData = inBillMapper.selectById(inBill.getId());
            inbillGoodsMapper.deleteByInBillCode(SecurityUtils.getUsername(),oldData.getInBillCode());
            inbillDetailMapper.deleteNoExist(SecurityUtils.getUsername(),oldData.getInBillCode(),null);
            //修改入库单抬头取盘状态为已取盘
            inBill.setTakeTrayStatus(TakeTrayStatusEnum.ALREADY.getCode());
        }
        return inBillMapper.updateById(inBill);
    }

    /**
     * 验证是否可以修改
     * @param
     * @return true可以，false不可以
     */
    public boolean canUpdate(String id){
        boolean b = false;
        InBill inBill = inBillMapper.selectById(id);
        if(inBill.getInBillStatus().equals(InBillEnum.WAIT.getCode())){
            b = true;
        }
        return b;
    }

    public void printData(InBillPrintDto dto, HttpServletResponse responseBody) {
        Map<String, Object> map = BeanUtil.beanToMap(dto);
        String tempId = configService.selectConfigByKey("wms.warehousing.inbill.tempId");
        Temp temp = tempService.selectWmsWarehouseTempByTempId(tempId);
        SysFile file =  sysFileService.selectSysFileById(temp.getFileKey());
        map.put("imgCode", ServletUtils.getRequest().getHeader("Origin")+map.get("printCodeList"));
        PdfUtil.createPDF(file.getPath(),map,responseBody);
    }
}
