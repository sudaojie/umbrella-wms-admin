package com.ruoyi.wms.basics.service;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.basics.domain.TrayModel;
import com.ruoyi.wms.basics.mapper.TrayModelMapper;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;
/**
 * 托盘规格Service接口
 *
 * @author ruoyi
 * @date 2023-02-21
 */
@Slf4j
@Service
public class TrayModelService extends ServiceImpl<TrayModelMapper, TrayModel> {

    @Autowired(required = false)
    private TrayModelMapper trayModelMapper;
    @Autowired
    protected Validator validator;
    /**
     * 查询托盘规格
     *
     * @param id 托盘规格主键
     * @return 托盘规格
     */
    public TrayModel selectSysTrayModelById(String id){
        QueryWrapper<TrayModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        return trayModelMapper.selectOne(queryWrapper);
    }


    /**
     * 根据ids查询托盘规格
     *
     * @param ids 托盘规格 IDs
     * @return 托盘规格
     */
    public List<TrayModel> selectSysTrayModelByIds(String[] ids) {
        QueryWrapper<TrayModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        queryWrapper.in("id", Arrays.asList(ids));
        return trayModelMapper.selectList(queryWrapper);
    }

    /**
     * 查询托盘规格列表
     *
     * @param sysTrayModel 托盘规格
     * @return 托盘规格集合
     */
    public List<TrayModel> selectSysTrayModelList(TrayModel sysTrayModel){
        QueryWrapper<TrayModel> queryWrapper = getQueryWrapper(sysTrayModel);
        return trayModelMapper.select(queryWrapper);
    }

    /**
     * 新增托盘规格
     *
     * @param sysTrayModel 托盘规格
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public TrayModel insertSysTrayModel(TrayModel sysTrayModel){
        sysTrayModel.setId(IdUtil.simpleUUID());
        sysTrayModel.setDelFlag(DelFlagEnum.DEL_NO.getCode());
        trayModelMapper.insert(sysTrayModel);
        return sysTrayModel;
    }

    /**
     * 修改托盘规格
     *
     * @param sysTrayModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TrayModel updateSysTrayModel(TrayModel sysTrayModel){
        trayModelMapper.updateById(sysTrayModel);
        return sysTrayModel;
    }

    /**
     * 批量删除托盘规格
     *
     * @param ids 需要删除的托盘规格主键集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysTrayModelByIds(String[] ids){
        List<TrayModel> sysTrayModels = new ArrayList<>();
        for (String id : ids) {
            TrayModel sysTrayModel = new TrayModel();
            sysTrayModel.setId(id);
            sysTrayModel.setDelFlag(DelFlagEnum.DEL_YES.getCode());
            sysTrayModels.add(sysTrayModel);
        }
        return super.updateBatchById(sysTrayModels) ? 1 : 0;
    }

    /**
     * 删除托盘规格信息
     *
     * @param id 托盘规格主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysTrayModelById(String id){
        TrayModel sysTrayModel = new TrayModel();
        sysTrayModel.setId(id);
        sysTrayModel.setDelFlag(DelFlagEnum.DEL_YES.getCode());
        return trayModelMapper.updateById(sysTrayModel);
    }

    public QueryWrapper<TrayModel> getQueryWrapper(TrayModel sysTrayModel) {
        QueryWrapper<TrayModel> queryWrapper = new QueryWrapper<>();
        if (sysTrayModel != null) {
            sysTrayModel.setDelFlag(DelFlagEnum.DEL_NO.getCode());
            queryWrapper.eq("del_flag",sysTrayModel.getDelFlag());
            //规格编号
            if (StrUtil.isNotEmpty(sysTrayModel.getTrayModelCode())) {
                queryWrapper.like("tray_model_code",sysTrayModel.getTrayModelCode());
            }
            //规格名称
            if (StrUtil.isNotEmpty(sysTrayModel.getTrayModelName())) {
                queryWrapper.like("tray_model_name",sysTrayModel.getTrayModelName());
            }
            //长
            if (sysTrayModel.getModelLength() != null) {
                queryWrapper.eq("model_length",sysTrayModel.getModelLength());
            }
            //宽
            if (sysTrayModel.getModelWidth() != null) {
                queryWrapper.eq("model_width",sysTrayModel.getModelWidth());
            }
            //高
            if (sysTrayModel.getModelHeight() != null) {
                queryWrapper.eq("model_height",sysTrayModel.getModelHeight());
            }
            //容量
            if (sysTrayModel.getModelVolume() != null) {
                queryWrapper.eq("model_volume",sysTrayModel.getModelVolume());
            }
            //限重
            if (sysTrayModel.getModelWeight() != null) {
                queryWrapper.eq("model_weight",sysTrayModel.getModelWeight());
            }
        }
        return queryWrapper;
    }

    /**
     * 数据导入实现
     * @param sysTrayModelList 模板数据
     * @param updateSupport 是否更新已经存在的数据
     * @param operName 操作人姓名
     * @return
     */
    public String importData(List<TrayModel> sysTrayModelList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(sysTrayModelList) || sysTrayModelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (TrayModel sysTrayModel : sysTrayModelList) {
            try {
                //根据唯一属性获取对应数据（自己修改）
                TrayModel u = null;
                //如果查询不到直接添加数据
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, sysTrayModel);
                    sysTrayModel.setId(IdUtil.simpleUUID());
                    sysTrayModel.setCreateBy(operName);
                    sysTrayModel.setCreateTime(new Date());
                    sysTrayModel.setDelFlag(DelFlagEnum.DEL_NO.getCode());
                    trayModelMapper.insert(sysTrayModel);
                    successNum++;
                } else if (updateSupport) {
                    BeanValidators.validateWithException(validator, sysTrayModel);
                    //todo 验证
                    sysTrayModel.setId(u.getId());
                    sysTrayModel.setUpdateBy(operName);
                    sysTrayModel.setUpdateTime(new Date());
                    trayModelMapper.updateById(sysTrayModel);
                    successNum++;
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
     * 获取托盘规格信息
     * @return
     */
    public List<Map> getTrayModelData() {
        return trayModelMapper.getTrayModelData();
    }

    /**
     * 根据规格编号查重
     * @param sysTrayModel
     * @return
     */
    public List<TrayModel> selectSysTrayModelByCode(TrayModel sysTrayModel) {
        return trayModelMapper.selectSysTrayModelByCode(sysTrayModel);
    }

    /**
     * 校验修改托盘规格信息是否重复
     * @param trayModel
     * @return
     */
    public List<TrayModel> checkData(TrayModel trayModel) {
        return trayModelMapper.checkData(trayModel);
    }
}
