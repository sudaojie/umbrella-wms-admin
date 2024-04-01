package com.ruoyi.file.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.file.service.UploadService;
import com.ruoyi.system.domain.vo.FormalFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件上传Controller
 *
 * @author yangjie
 * @date 2022-10-28
 */
@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 临时文件上传
     *
     * @param file 文件
     */
    @RequestMapping("/temp/upload")
    public AjaxResult tempUpload(@RequestParam("file") MultipartFile file) {
        return AjaxResult.success(uploadService.tempUpload(file));
    }

    /**
     * 正式文件上传
     *
     * @param fileVo 文件
     */
    @RequestMapping("/formal/upload")
    public AjaxResult formalUpload(@RequestBody FormalFileVo fileVo) {
        uploadService.formalUpload(fileVo);
        return AjaxResult.success();
    }

}
