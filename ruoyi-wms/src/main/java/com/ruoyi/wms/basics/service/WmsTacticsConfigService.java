package com.ruoyi.wms.basics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.enums.InBillStatusEnum;
import com.ruoyi.common.enums.OutBillStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wcs.domain.WcsDeviceBaseInfo;
import com.ruoyi.wcs.service.WcsDeviceBaseInfoService;
import com.ruoyi.wms.basics.domain.WmsTacticsConfig;
import com.ruoyi.wms.basics.dto.WmsTacticsConfigEditDto;
import com.ruoyi.wms.basics.mapper.WmsTacticsConfigMapper;
import com.ruoyi.wms.check.service.WmsWarehouseCheckDetailService;
import com.ruoyi.wms.enums.DryInbillStatusEnum;
import com.ruoyi.wms.enums.DryOutbillStatusTypeEnum;
import com.ruoyi.wms.enums.MoveEnum;
import com.ruoyi.wms.move.domain.WmsMoveList;
import com.ruoyi.wms.nolist.domain.NolistWait;
import com.ruoyi.wms.outbound.domain.OutBill;
import com.ruoyi.wms.stock.domain.DryOutbill;
import com.ruoyi.wms.stock.domain.WmsDryInbill;
import com.ruoyi.wms.stock.mapper.DryOutbillMapper;
import com.ruoyi.wms.stock.mapper.WmsDryInbillMapper;
import com.ruoyi.wms.warehousing.domain.InBill;
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
 * 货物类型托盘取盘回盘策略配置Service接口
 *
 * @author ruoyi
 * @date 2023-02-24
 */
@Slf4j
@Service
public class WmsTacticsConfigService extends ServiceImpl<WmsTacticsConfigMapper, WmsTacticsConfig> {

    @Autowired(required = false)
    private WmsTacticsConfigMapper wmsTacticsConfigMapper;

    @Autowired(required = false)
    private WmsDryInbillMapper wmsDryInbillMapper;

    @Autowired(required = false)
    private DryOutbillMapper dryOutbillMapper;

    @Autowired(required = false)
    private WcsDeviceBaseInfoService wcsDeviceBaseInfoService;

    @Autowired
    private WmsWarehouseCheckDetailService checkDetailService;

    @Autowired
    protected Validator validator;

    /**
     * 查询货物类型托盘取盘回盘策略配置
     *
     * @param id 货物类型托盘取盘回盘策略配置主键
     * @return 货物类型托盘取盘回盘策略配置
     */
    public WmsTacticsConfig selectWmsTacticsConfigById(String id) {
        QueryWrapper<WmsTacticsConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return wmsTacticsConfigMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询货物类型托盘取盘回盘策略配置
     *
     * @param ids 货物类型托盘取盘回盘策略配置 IDs
     * @return 货物类型托盘取盘回盘策略配置
     */
    public List<WmsTacticsConfig> selectWmsTacticsConfigByIds(String[] ids) {
        QueryWrapper<WmsTacticsConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return wmsTacticsConfigMapper.selectList(queryWrapper);
    }

    /**
     * 查询货物类型托盘取盘回盘策略配置列表
     *
     * @param wmsTacticsConfig 货物类型托盘取盘回盘策略配置
     * @return 货物类型托盘取盘回盘策略配置集合
     */
    public List<WmsTacticsConfig> selectWmsTacticsConfigList(WmsTacticsConfig wmsTacticsConfig) {
        QueryWrapper<WmsTacticsConfig> queryWrapper = getQueryWrapper(wmsTacticsConfig);
        List<WmsTacticsConfig> listConfig = wmsTacticsConfigMapper.select(queryWrapper);
        for (WmsTacticsConfig config : listConfig) {
            if (StringUtils.isNotEmpty(config.getTacticsContent())) {
                config.setArrTacticsContent(config.getTacticsContent().split(","));
            }
        }
        return listConfig;
    }

    /**
     * 新增货物类型托盘取盘回盘策略配置
     *
     * @param wmsTacticsConfig 货物类型托盘取盘回盘策略配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsTacticsConfig insertWmsTacticsConfig(WmsTacticsConfig wmsTacticsConfig) {
        //添加策略
        StringBuilder stringBuilder = new StringBuilder();
        wmsTacticsConfig.setId(IdUtil.simpleUUID());
        wmsTacticsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        //存入多条库区编码
        if (StringUtils.isNotEmpty(wmsTacticsConfig.getArrTacticsContent())) {
            System.out.println(wmsTacticsConfig.getArrTacticsContent()[0]);
            for (int i = 0; i < wmsTacticsConfig.getArrTacticsContent().length; i++) {
                stringBuilder.append(i < wmsTacticsConfig.getArrTacticsContent().length - 1 ?
                        wmsTacticsConfig.getArrTacticsContent()[i] + "," : wmsTacticsConfig.getArrTacticsContent()[i]);
            }
            wmsTacticsConfig.setTacticsContent(stringBuilder.toString());
        }
        wmsTacticsConfigMapper.insert(wmsTacticsConfig);
        return wmsTacticsConfig;
    }

    public QueryWrapper<InBill> getQueryInbillStatusWrapper() {
        QueryWrapper<InBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("in_bill_status", InBillStatusEnum.ONE.getCode(), InBillStatusEnum.TOW.getCode(), InBillStatusEnum.THREE.getCode());
        return queryWrapper;
    }

    public QueryWrapper<OutBill> getQueryOutbillStatusWrapper() {
        QueryWrapper<OutBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("out_bill_status", OutBillStatusEnum.WAIT.getCode(), OutBillStatusEnum.OUTPROCESS.getCode());
        return queryWrapper;
    }

    public QueryWrapper<WmsMoveList> getQueryMoveStatusWrapper() {
        QueryWrapper<WmsMoveList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("move_status", MoveEnum.unexecuted.getCode(), MoveEnum.executing.getCode());
        return queryWrapper;
    }

    public QueryWrapper<DryOutbill> getQueryDryOutStatusWrapper() {
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("dry_outbill_status", DryOutbillStatusTypeEnum.WAIT.getCode(), DryOutbillStatusTypeEnum.IN.getCode());
        return queryWrapper;
    }

    public QueryWrapper<DryOutbill> getQueryDryOutEndWrapper() {
        QueryWrapper<DryOutbill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("dry_outbill_status", DryOutbillStatusTypeEnum.END.getCode());
        return queryWrapper;
    }

    public QueryWrapper<NolistWait> getQueryNoListWrapper() {
        QueryWrapper<NolistWait> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return queryWrapper;
    }

    /**
     * 修改货物类型托盘取盘回盘策略配置
     *
     * @param wmsTacticsConfigEditDto 货物类型托盘取盘回盘策略配置 编辑dto
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateWmsTacticsConfig(WmsTacticsConfigEditDto wmsTacticsConfigEditDto) {
        StringBuilder successMsg = new StringBuilder();
        //校验是否单据流转
        // this.checkBillStatus(wmsTacticsConfigEditDto.getAgvDevices());
        for (WmsTacticsConfig wmsTacticsConfig : wmsTacticsConfigEditDto.getWmsTacticsConfigList()) {
            StringBuilder stringBuilder = new StringBuilder();
            //存入多条库区编码
            if (StringUtils.isNotEmpty(wmsTacticsConfig.getArrTacticsContent())) {
                for (int i = 0; i < wmsTacticsConfig.getArrTacticsContent().length; i++) {
                    stringBuilder.append(i < wmsTacticsConfig.getArrTacticsContent().length - 1 ?
                            wmsTacticsConfig.getArrTacticsContent()[i] + "," : wmsTacticsConfig.getArrTacticsContent()[i]);
                }
                wmsTacticsConfig.setTacticsContent(stringBuilder.toString());
            } else {
                wmsTacticsConfig.setTacticsContent(null);
            }
            wmsTacticsConfigMapper.updateById(wmsTacticsConfig);
        }

        //批量修改agv设备的启用/禁用
        wcsDeviceBaseInfoService.updateBatchById(wmsTacticsConfigEditDto.getAgvDevices());

        successMsg.append("操作成功");
        return successMsg.toString();
    }

    /**
     * 校验是否还有未执行完成的单据
     *
     * @return
     */
    public void checkBillStatus(List<WcsDeviceBaseInfo> agvDevices) {
        boolean agvLightNoChange = true;
        boolean agvStoreNoChange = true;

        QueryWrapper<WcsDeviceBaseInfo> agvLightWrapper = new QueryWrapper<>();
        agvLightWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        agvLightWrapper.eq("id", agvDevices.get(0).getId());
        WcsDeviceBaseInfo agvLight = wcsDeviceBaseInfoService.getBaseMapper().selectOne(agvLightWrapper);
        agvLightNoChange = agvLight.getEnableStatus().equals(agvDevices.get(0).getEnableStatus());

        QueryWrapper<WcsDeviceBaseInfo> agvStoreWrapper = new QueryWrapper<>();
        agvStoreWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        agvStoreWrapper.eq("id", agvDevices.get(1).getId());
        WcsDeviceBaseInfo agvStore = wcsDeviceBaseInfoService.getBaseMapper().selectOne(agvStoreWrapper);
        agvStoreNoChange = agvStore.getEnableStatus().equals(agvDevices.get(1).getEnableStatus());

        if (!agvLightNoChange) {
            //校验晾晒出库单状态，不存在（待出库，出库中）
            QueryWrapper<DryOutbill> queryDryOutbillWrapper = getQueryDryOutStatusWrapper();
            int dryOutbillCount = wmsTacticsConfigMapper.selectDryOutbillStatus(queryDryOutbillWrapper);
            if (dryOutbillCount > 0) {
                throw new ServiceException("存在未完结的晾晒出库单数据，无法更改！");
            }


            QueryWrapper<DryOutbill> queryDryOutbillEndWrapper = getQueryDryOutEndWrapper();
            List<DryOutbill> dryOutEndbills = dryOutbillMapper.selectList(queryDryOutbillEndWrapper);
            if(CollUtil.isNotEmpty(dryOutEndbills)){
                List<String> dryOutBillCodes = dryOutEndbills.parallelStream()
                                                .map(DryOutbill::getDryOutbillCode)
                                                .collect(Collectors.toList());
                LambdaQueryWrapper<WmsDryInbill> haveDryInbillQueryWrapper = Wrappers.lambdaQuery();
                haveDryInbillQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                                         .in(WmsDryInbill::getDryOutbillCode,dryOutBillCodes);
                int count = wmsDryInbillMapper.selectCount(haveDryInbillQueryWrapper).intValue();
                if(count == 0){
                    throw new ServiceException("存在未完结的晾晒出库单和晾晒入库单数据，无法更改！");
                }
            }


            //校验晾晒入库单状态，不存在（0、待入库 1、组盘中 2、已组盘 3.入库中）
            LambdaQueryWrapper<WmsDryInbill> dryInbillQueryWrapper = Wrappers.lambdaQuery();
            dryInbillQueryWrapper.eq(BaseEntity::getDelFlag, DelFlagEnum.DEL_NO.getCode())
                    .in(WmsDryInbill::getDryInbillStatus, DryInbillStatusEnum.WAIT.getCode(), DryInbillStatusEnum.GROUPIN.getCode(),
                            DryInbillStatusEnum.TAKE.getCode(), DryInbillStatusEnum.ING.getCode());
            if (wmsDryInbillMapper.selectCount(dryInbillQueryWrapper) > 0) {
                throw new ServiceException("存在未完结的晾晒入库单数据，无法更改！");
            }
        }

        if (!agvStoreNoChange) {
            //校验入库单状态，不存在（待收货，已收货，上架中）
            QueryWrapper<InBill> queryInWrapper = getQueryInbillStatusWrapper();
            int inbillCount = wmsTacticsConfigMapper.selectInbillStatus(queryInWrapper);
            if (inbillCount > 0) {
                throw new ServiceException("存在未完结的入库单数据，无法更改！");
            }
            //校验出库单状态，不存在（待拣货，拣货中）
            QueryWrapper<OutBill> queryOutbillWrapper = getQueryOutbillStatusWrapper();
            int outbillCount = wmsTacticsConfigMapper.selectOutbillStatus(queryOutbillWrapper);
            if (outbillCount > 0) {
                throw new ServiceException("存在未完结的出库单数据，无法更改！");
            }
            //校验移库单状态，不存在（未移库, 移库中）
            QueryWrapper<WmsMoveList> queryMovebillWrapper = getQueryMoveStatusWrapper();
            int movebillCount = wmsTacticsConfigMapper.selectMovebillStatus(queryMovebillWrapper);
            if (movebillCount > 0) {
                throw new ServiceException("存在未完结的移库单数据，无法更改！");
            }
            //无单上下架，不存在未删除
            QueryWrapper<NolistWait> queryNoListWrapper = getQueryNoListWrapper();
            int noListCount = wmsTacticsConfigMapper.selectNoListStatus(queryNoListWrapper);
            if (noListCount > 0) {
                throw new ServiceException("存在未完结的无单上下架数据，无法更改！");
            }

            if (checkDetailService.haveChecking()) {
                throw new ServiceException("存在未完结的盘点数据，无法更改！");
            }
        }
    }

    /**
     * 批量删除货物类型托盘取盘回盘策略配置
     *
     * @param ids 需要删除的货物类型托盘取盘回盘策略配置主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTacticsConfigByIds(String[] ids) {
        List<WmsTacticsConfig> wmsTacticsConfigs = new ArrayList<>();
        for (String id : ids) {
            WmsTacticsConfig wmsTacticsConfig = new WmsTacticsConfig();
            wmsTacticsConfig.setId(id);
            wmsTacticsConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            wmsTacticsConfigs.add(wmsTacticsConfig);
        }
        return super.updateBatchById(wmsTacticsConfigs) ? 1 : 0;
    }

    /**
     * 删除货物类型托盘取盘回盘策略配置信息
     *
     * @param id 货物类型托盘取盘回盘策略配置主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTacticsConfigById(String id) {
        WmsTacticsConfig wmsTacticsConfig = new WmsTacticsConfig();
        wmsTacticsConfig.setId(id);
        wmsTacticsConfig.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return wmsTacticsConfigMapper.updateById(wmsTacticsConfig);
    }

    public QueryWrapper<WmsTacticsConfig> getQueryWrapper(WmsTacticsConfig wmsTacticsConfig) {
        QueryWrapper<WmsTacticsConfig> queryWrapper = new QueryWrapper<>();
        if (wmsTacticsConfig != null) {
            wmsTacticsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag", wmsTacticsConfig.getDelFlag());
            //策略(0-平均分配；1-集中堆放)
            if (StrUtil.isNotEmpty(wmsTacticsConfig.getTactics())) {
                queryWrapper.eq("tactics", wmsTacticsConfig.getTactics());
            }
            //集中堆放策略的库区顺序
            if (StrUtil.isNotEmpty(wmsTacticsConfig.getTacticsContent())) {
                queryWrapper.eq("tactics_content", wmsTacticsConfig.getTacticsContent());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     *
     * @param wmsTacticsConfigList 模板数据
     * @param updateSupport        是否更新已经存在的数据
     * @param operName             操作人姓名
     * @return
     */
    public String importData(List<WmsTacticsConfig> wmsTacticsConfigList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(wmsTacticsConfigList) || wmsTacticsConfigList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (WmsTacticsConfig wmsTacticsConfig : wmsTacticsConfigList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                WmsTacticsConfig u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, wmsTacticsConfig);
                    wmsTacticsConfig.setId(IdUtil.simpleUUID());
                    wmsTacticsConfig.setCreateBy(operName);
                    wmsTacticsConfig.setCreateTime(new Date());
                    wmsTacticsConfig.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    wmsTacticsConfigMapper.insert(wmsTacticsConfig);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, wmsTacticsConfig);
                    wmsTacticsConfig.setId(u.getId());
                    wmsTacticsConfig.setUpdateBy(operName);
                    wmsTacticsConfig.setUpdateTime(new Date());
                    wmsTacticsConfigMapper.updateById(wmsTacticsConfig);
                    successNum++;
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
}
