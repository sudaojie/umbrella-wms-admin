package com.ruoyi.wms.group.disk.data.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskDataInfo;
import com.ruoyi.wms.group.disk.data.domain.WmsGroupDiskGoodsInfo;
import com.ruoyi.wms.group.disk.data.domain.vo.WmsGroupDiskDataInfoVO;
import com.ruoyi.wms.group.disk.data.domain.vo.WmsUnGroupGoodsInfoVO;
import com.ruoyi.wms.group.disk.data.domain.vo.WmsUnGroupVO;
import com.ruoyi.wms.group.disk.data.mapper.WmsGroupDiskDataInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.wms.warehousing.domain.ListingDetail;
import com.ruoyi.wms.warehousing.mapper.InbillGoodsMapper;
import com.ruoyi.wms.warehousing.mapper.ListingDetailMapper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * wms已组盘数据信息Service接口
 *
 * @author hewei
 * @date 2023-04-19
 */
@Slf4j
@Service
public class WmsGroupDiskDataInfoService extends ServiceImpl<WmsGroupDiskDataInfoMapper, WmsGroupDiskDataInfo> {

    @Resource
    private ListingDetailMapper listingDetailMapper;

    @Resource
    private InbillGoodsMapper inbillGoodsMapper;

    /**
     * 查询wms已组盘数据信息列表
     *
     * @param wmsGroupDiskDataInfo wmsGroupDiskDataInfo
     * @return list
     */
    public List<WmsGroupDiskDataInfo> selectWmsGroupDiskDataInfoList(WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        QueryWrapper<ListingDetail> listingDetailQuery = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(wmsGroupDiskDataInfo)) {
            if (StrUtil.isNotEmpty(wmsGroupDiskDataInfo.getTrayCode())) {
                listingDetailQuery.like("wld.tray_code", wmsGroupDiskDataInfo.getTrayCode());
            }
        }
        listingDetailQuery.eq("wld.del_flag", DelFlagEnum.DEL_NO.getCode()).groupBy("wld.tray_code");
        listingDetailQuery.orderByDesc("wld.create_time");
        return listingDetailMapper.selectWmsGroupDiskDataInfoList(listingDetailQuery);
    }

    /**
     * 查询wms已组盘托盘上货物信息列表
     *
     * @param wmsGroupDiskGoodsInfo wmsGroupDiskGoodsInfo
     * @return list
     */
    public List<WmsGroupDiskGoodsInfo> getGoodsInfoOnGroupTray(WmsGroupDiskGoodsInfo wmsGroupDiskGoodsInfo) {
        QueryWrapper<ListingDetail> listingDetailQuery = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(wmsGroupDiskGoodsInfo)) {
            if (StrUtil.isNotEmpty(wmsGroupDiskGoodsInfo.getTrayCode())) {
                listingDetailQuery.in("wld.tray_code", Arrays.asList(wmsGroupDiskGoodsInfo.getTrayCode().split(",")));
            } else {
                throw new ServiceException("托盘编码缺失, 获取货物明细异常");
            }
            if (StrUtil.isNotEmpty(wmsGroupDiskGoodsInfo.getInBillCode())) {
                listingDetailQuery.like("wld.in_bill_code", wmsGroupDiskGoodsInfo.getInBillCode());
            }
            if (StrUtil.isNotEmpty(wmsGroupDiskGoodsInfo.getPartsCode())) {
                listingDetailQuery.like("wld.parts_code", wmsGroupDiskGoodsInfo.getPartsCode());
            }
            if (StrUtil.isNotEmpty(wmsGroupDiskGoodsInfo.getGoodsName())) {
                listingDetailQuery.like("wld.goods_name", wmsGroupDiskGoodsInfo.getGoodsName());
            }
        } else {
            throw new ServiceException("业务数据缺失");
        }

        listingDetailQuery.eq("wld.del_flag", DelFlagEnum.DEL_NO.getCode());

        listingDetailQuery.orderByDesc("wld.create_time");
        return listingDetailMapper.getGoodsInfoOnGroupTray(listingDetailQuery);
    }

    /**
     * 查询wms PDA 已组盘数据信息列表
     *
     * @param wmsGroupDiskDataInfo wmsGroupDiskDataInfo
     * @return result
     */
    public List<WmsGroupDiskDataInfoVO> selectWmsGroupDiskDataInfoListPda(WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        List<WmsGroupDiskDataInfoVO> result = new ArrayList<>();
        List<String> trayCodeList;
        List<WmsGroupDiskDataInfo> list = selectWmsGroupDiskDataInfoList(wmsGroupDiskDataInfo);
        if (CollUtil.isNotEmpty(list)) {
            List<WmsGroupDiskGoodsInfo> goodsInfos = new ArrayList<>();
            trayCodeList = list.stream().map(WmsGroupDiskDataInfo::getTrayCode).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(trayCodeList)) {
                WmsGroupDiskGoodsInfo wmsGroupDiskGoodsInfo = new WmsGroupDiskGoodsInfo();
                wmsGroupDiskGoodsInfo.setTrayCode(CollUtil.join(trayCodeList, ","));
                goodsInfos = getGoodsInfoOnGroupTray(wmsGroupDiskGoodsInfo);
            }
            if (CollUtil.isNotEmpty(goodsInfos)) {
                for (WmsGroupDiskDataInfo item : list) {
                    WmsGroupDiskDataInfoVO wmsGroupDiskDataInfoVO = new WmsGroupDiskDataInfoVO();
                    BeanUtil.copyProperties(item, wmsGroupDiskDataInfoVO);
                    String trayCode = item.getTrayCode();
                    if (StrUtil.isNotEmpty(trayCode)) {
                        wmsGroupDiskDataInfoVO.setList(goodsInfos.parallelStream().filter(e -> trayCode.equals(e.getTrayCode())).collect(Collectors.toList()));
                    }
                    result.add(wmsGroupDiskDataInfoVO);
                }
            } else {
                for (WmsGroupDiskDataInfo item : list) {
                    WmsGroupDiskDataInfoVO wmsGroupDiskDataInfoVO = new WmsGroupDiskDataInfoVO();
                    BeanUtil.copyProperties(item, wmsGroupDiskDataInfoVO);
                    result.add(wmsGroupDiskDataInfoVO);
                }
            }
        }
        return result;
    }

    /**
     * 查询 PDA 未组盘数据信息列表
     *
     * @return list
     */
    public List<WmsUnGroupVO> selectUnGroupDiskGoodsList(WmsGroupDiskDataInfo wmsGroupDiskDataInfo) {
        List<WmsUnGroupGoodsInfoVO> list = inbillGoodsMapper.selectUnGroupDiskGoodsList();
        Map<String, List<WmsUnGroupGoodsInfoVO>> resultMap = new HashMap<>();
        List<WmsUnGroupVO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            resultMap = list.parallelStream().collect(Collectors.groupingBy(WmsUnGroupGoodsInfoVO::getInBillCode));
        }
        if (CollUtil.isNotEmpty(resultMap)) {
            resultMap.forEach((k,v) ->{
                WmsUnGroupVO wmsUnGroupVO = new WmsUnGroupVO();
                wmsUnGroupVO.setInBillCode(k);
                wmsUnGroupVO.setList(v);
                result.add(wmsUnGroupVO);
            });
        }
        return result;
    }
}
