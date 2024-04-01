package com.ruoyi.file.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.enums.DelFlagEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServerConfigUtil;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MinIoUtil;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.domain.SysFileTemp;
import com.ruoyi.system.domain.vo.FormalFileVo;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.system.service.ISysFileTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 附件上传Service
 *
 * @author yangjie
 * @date 2022-10-28
 */
@Slf4j
@Service
public class UploadService {

    @Value("${service.uploadType}")
    private String uploadType;


    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private ISysFileTempService sysFileTempService;

    @Autowired
    private MinIoUtil minioUtil;


    /**
     * 临时文件上传
     *
     * @param file
     * @return
     */
    public SysFileTemp tempUpload(MultipartFile file) {
        if (uploadType.equals(Constants.UPLOAD_TYPE_LOCAL)) {
            return localTempUpload(file);
        } else if (uploadType.equals(Constants.UPLOAD_TYPE_MINIO)) {
            return minIoTempUpload(file);
        } else {
            throw new ServiceException("附件只支持本地上传、MinIo上传");
        }
    }

    /**
     * 临时文件上传(本地)
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public SysFileTemp localTempUpload(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                // 上传文件路径
                String filePath = RuoYiConfig.getUploadTemporaryPath();

                String fileName = file.getOriginalFilename();
                String suffix = fileName.substring(fileName.lastIndexOf("."));

                String uploadFileName = FileUploadUtils.upload(filePath, file);
                String newFileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);

                String path = RuoYiConfig.getUploadTemporaryPath() + "/" + newFileName;

                //保存到文件临时表
                SysFileTemp sysFileTemp = new SysFileTemp();
                sysFileTemp.setId(IdUtil.fastSimpleUUID());
                sysFileTemp.setDisplayName(fileName);
                sysFileTemp.setName(newFileName);
                sysFileTemp.setPath(path);
                sysFileTemp.setSize(file.getSize());
                sysFileTemp.setType(suffix);
                String visitUrl = ServerConfigUtil.getUrl() + uploadFileName;
                sysFileTemp.setVisitUrl(visitUrl);
                boolean isSuccess = sysFileTempService.save(sysFileTemp);
                if (!isSuccess) {
                    throw new ServiceException("上传附件异常");
                }
                return sysFileTemp;
            } else {
                throw new ServiceException("附件信息不能为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 正式文件上传
     *
     * @param fileVo
     */
    public void formalUpload(FormalFileVo fileVo) {
        if (fileVo != null && StrUtil.isNotBlank(fileVo.getBuniessId())) {
            if (StrUtil.isBlank(fileVo.getTempIds())) {
                deleteLocalFile(fileVo.getBuniessId());
            } else {
                convertFromSysFileTemp(fileVo);
            }
        }
    }

    /**
     * 清理本地附件
     * @param buinessId
     */
    public void deleteLocalFile(String buinessId){
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buiness_id", buinessId);
        queryWrapper.eq("del_flag", DelFlagEnum.DEL_NO.getCode());
        List<SysFile> fileList = sysFileService.getBaseMapper().selectList(queryWrapper);
        deleteLocalFiles(fileList);
    }

    /**
     * 删除本地多个附件
     * @param sysFileList
     */
    public void deleteLocalFiles(List<SysFile> sysFileList) {
        if (CollectionUtil.isNotEmpty(sysFileList)) {
            List<String> idList = new ArrayList<>();
            for (SysFile sysFile : sysFileList) {
                log.debug("删除文件：" + sysFile.getPath());
                //FileUtil.delByPath(sysFile.getPath());
                //不删除文件假删除
                idList.add(sysFile.getId());
            }
            //删除变成修改
            UpdateWrapper<SysFile> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("del_flag", DelFlagEnum.DEL_YES.getCode());
            updateWrapper.in("id",idList);
            sysFileService.update(updateWrapper);
        }
    }


    /**
     * 临时附件转换
     * @param fileVo
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public List<SysFile> convertFromSysFileTemp(FormalFileVo fileVo) {
        List<String> ids = Arrays.asList(fileVo.getTempIds().split(","));
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        // 移除空字符串
        ids = ids.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
        // 查询临时文件表 并且 排除已在正式环境存在的数据 得到真正要上传的数据
        QueryWrapper<SysFileTemp> tempQueryWrapper = new QueryWrapper<>();
        tempQueryWrapper.in("id", ids);
        tempQueryWrapper.notInSql("name","select name from sys_file where buiness_id ='"+fileVo.getBuniessId()+"'");
        List<SysFileTemp> sysFileTemps = sysFileTempService.list(tempQueryWrapper);
        List<SysFile> sysFileList = new ArrayList<>();
        List<String> addIds = new ArrayList<>();
        for (SysFileTemp sysFileTemp : sysFileTemps) {
            File newFile = FileUploadUtils.moveToFormal(sysFileTemp.getPath());
            String newPath = newFile.getPath();
            SysFile sysFile = new SysFile();
            BeanUtil.copyProperties(sysFileTemp, sysFile);
            sysFile.setBuinessId(fileVo.getBuniessId());
            sysFile.setPath(newPath);
            sysFileList.add(sysFile);
            addIds.add(sysFile.getId());
        }
        addIds.addAll(ids);
        // 保存数据到正式表
        if(CollUtil.isNotEmpty(sysFileList)){
            sysFileService.saveBatch(sysFileList, sysFileList.size());
        }
        // 清理临时数据和临时文件
        sysFileTempService.cleanTempDataAndTempFiles(sysFileTemps);
        // 查询本次需要保存之外的其它数据和文件 将其移除
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buiness_id", fileVo.getBuniessId());
        queryWrapper.notIn("id", addIds);
        List<SysFile> fileList = sysFileService.getBaseMapper().selectList(queryWrapper);
        deleteLocalFiles(fileList);
        return sysFileList;
    }



    /**
     * 临时文件上传(MinIo)
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public SysFileTemp minIoTempUpload(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                // 判断桶是否存在
                if (!minioUtil.bucketExists(MinIoUtil.TEMPORARY)) {
                    minioUtil.makeBucket(MinIoUtil.TEMPORARY);
                }
                String fileName = file.getOriginalFilename();
                String contentType = file.getContentType();
                if (StrUtil.isNotBlank(fileName)) {
                    SysFileTemp sysFileTemp = new SysFileTemp();
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    String newName = MinIoUtil.getDatePath() + "/" + IdUtil.randomUUID() + suffix;
                    InputStream inputStream = file.getInputStream();
                    minioUtil.putObject(MinIoUtil.TEMPORARY, newName, inputStream, contentType);
                    inputStream.close();

                    //保存到文件临时表
                    sysFileTemp.setName(newName);
                    sysFileTemp.setDisplayName(fileName);
                    sysFileTemp.setPath(newName);
                    sysFileTemp.setSize(file.getSize());
                    sysFileTemp.setType(suffix);

                    sysFileTemp.setVisitUrl(minioUtil.getPreviewUrl(MinIoUtil.TEMPORARY, newName));
                    boolean isSuccess = sysFileTempService.save(sysFileTemp);
                    if (!isSuccess) {
                        throw new ServiceException("上传附件异常");
                    }
                    return sysFileTemp;
                }
            } else {
                throw new ServiceException("附件信息不能为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ServiceException("获取文件信息失败，请重试");
    }

}
