package com.ruoyi.wms.check.service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.wms.check.domain.CheckGoods;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.wms.check.mapper.CheckGoodsMapper;
import com.ruoyi.wms.check.domain.CheckGoods;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 库存盘点货物单Service接口
 *
 * @author ruoyi
 * @date 2023-03-20
 */
@Slf4j
@Service
public class CheckGoodsService extends ServiceImpl<CheckGoodsMapper, CheckGoods> {

    @Autowired
    private CheckGoodsMapper checkGoodsMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询库存盘点货物单
     *
     * @param id 库存盘点货物单主键
     * @return 库存盘点货物单
     */
    public CheckGoods selectCheckGoodsById(String id){
        QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return checkGoodsMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询库存盘点货物单
     *
     * @param ids 库存盘点货物单 IDs
     * @return 库存盘点货物单
     */
    public List<CheckGoods> selectCheckGoodsByIds(String[] ids) {
        QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return checkGoodsMapper.selectList(queryWrapper);
    }

    /**
     * 查询库存盘点货物单列表
     *
     * @param checkGoods 库存盘点货物单
     * @return 库存盘点货物单集合
     */
    public List<CheckGoods> selectCheckGoodsList(CheckGoods checkGoods){
        QueryWrapper<CheckGoods> queryWrapper = getQueryWrapper(checkGoods);
        return checkGoodsMapper.select(queryWrapper);
    }

    /**
     * 新增库存盘点货物单
     *
     * @param checkGoods 库存盘点货物单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckGoods insertCheckGoods(CheckGoods checkGoods){
        checkGoods.setId(IdUtil.simpleUUID());
        checkGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        checkGoodsMapper.insert(checkGoods);
        return checkGoods;
    }

    /**
     * 修改库存盘点货物单
     *
     * @param checkGoods 库存盘点货物单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckGoods updateCheckGoods(CheckGoods checkGoods){
        checkGoodsMapper.updateById(checkGoods);
        return checkGoods;
    }

    /**
     * 批量删除库存盘点货物单
     *
     * @param ids 需要删除的库存盘点货物单主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckGoodsByIds(String[] ids){
        List<CheckGoods> checkGoodss = new ArrayList<>();
        for (String id : ids) {
            CheckGoods checkGoods = new CheckGoods();
            checkGoods.setId(id);
            checkGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            checkGoodss.add(checkGoods);
        }
        return super.updateBatchById(checkGoodss) ? 1 : 0;
    }

    /**
     * 删除库存盘点货物单信息
     *
     * @param id 库存盘点货物单主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteCheckGoodsById(String id){
        CheckGoods checkGoods = new CheckGoods();
        checkGoods.setId(id);
        checkGoods.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return checkGoodsMapper.updateById(checkGoods);
    }

    public QueryWrapper<CheckGoods> getQueryWrapper(CheckGoods checkGoods) {
        QueryWrapper<CheckGoods> queryWrapper = new QueryWrapper<>();
        if (checkGoods != null) {
            checkGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",checkGoods.getDelFlag());
            //盘点详情主键
            if (StrUtil.isNotEmpty(checkGoods.getCheckDetail())) {
                queryWrapper.eq("check_detail",checkGoods.getCheckDetail());
            }
            //货物编码
            if (StrUtil.isNotEmpty(checkGoods.getGoodsCode())) {
                queryWrapper.eq("goods_code",checkGoods.getGoodsCode());
            }
            //货物名称
            if (StrUtil.isNotEmpty(checkGoods.getGoodsName())) {
                queryWrapper.like("goods_name",checkGoods.getGoodsName());
            }
            //机件号
            if (StrUtil.isNotEmpty(checkGoods.getPartsCode())) {
                queryWrapper.eq("parts_code",checkGoods.getPartsCode());
            }
            //货物唯一码
            if (StrUtil.isNotEmpty(checkGoods.getOnlyCode())) {
                queryWrapper.eq("only_code",checkGoods.getOnlyCode());
            }
            //盘点托盘编号
            if (StrUtil.isNotEmpty(checkGoods.getTrayCode())) {
                queryWrapper.eq("tray_code",checkGoods.getTrayCode());
            }
            //盘点托盘名称
            if (StrUtil.isNotEmpty(checkGoods.getTrayName())) {
                queryWrapper.like("tray_name",checkGoods.getTrayName());
            }
            //盘点库位编号
            if (StrUtil.isNotEmpty(checkGoods.getLocationCode())) {
                queryWrapper.eq("location_code",checkGoods.getLocationCode());
            }
            //规格型号
            if (StrUtil.isNotEmpty(checkGoods.getModel())) {
                queryWrapper.eq("model",checkGoods.getModel());
            }
            //计量单位
            if (StrUtil.isNotEmpty(checkGoods.getMeasureUnit())) {
                queryWrapper.eq("measure_unit",checkGoods.getMeasureUnit());
            }
            //盘亏数量
            if (StrUtil.isNotEmpty(checkGoods.getLossNum())) {
                queryWrapper.eq("loss_num",checkGoods.getLossNum());
            }
            //盘盈数量
            if (StrUtil.isNotEmpty(checkGoods.getProfitNum())) {
                queryWrapper.eq("profit_num",checkGoods.getProfitNum());
            }
            //打印状态
            if (StrUtil.isNotEmpty(checkGoods.getPrintStatus())) {
                queryWrapper.eq("print_status",checkGoods.getPrintStatus());
            }
            //盘点单号
            if (StrUtil.isNotEmpty(checkGoods.getCheckBillCode())) {
                queryWrapper.eq("check_bill_code",checkGoods.getCheckBillCode());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param checkGoodsList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<CheckGoods> checkGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(checkGoodsList) || checkGoodsList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CheckGoods checkGoods : checkGoodsList) {
            if(null==checkGoods){
                throw new RuntimeException("导入数据模板不正确，请重新选择");
            }
            try {
                //根据唯一属性获取对应数据（自己修改）
                CheckGoods u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, checkGoods);
                    checkGoods.setId(IdUtil.simpleUUID());
                    checkGoods.setCreateBy(operName);
                    checkGoods.setCreateTime(new Date());
                    checkGoods.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    checkGoodsMapper.insert(checkGoods);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, checkGoods);
                    //todo 验证
                    //int count = checkGoodsMapper.checkCode(checkGoods);
                    //if(count>0){//判断是否重复
                        //failureNum++;
                        //failureMsg.append("<br/>" + failureNum + "、第"+failureNum+"行数据导入失败，数据重复；");
                    //}else{
                        checkGoods.setId(u.getId());
                        checkGoods.setUpdateBy(operName);
                        checkGoods.setUpdateTime(new Date());
                        checkGoodsMapper.updateById(checkGoods);
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
}
