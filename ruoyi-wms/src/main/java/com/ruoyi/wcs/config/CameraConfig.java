package com.ruoyi.wcs.config;

import cn.hutool.system.SystemUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author hewei
 * @date 2022/9/8 18:22
 **/
@Data
@Component
@ConfigurationProperties(prefix = "camera")
public class CameraConfig {

    /**
     * 是否启用摄像头
     */
    private Boolean enable;

    /**
     * 录像文件上传路径
     */
    private String windowsVideoUploadPath;

    private String linuxVideoUploadPath;

    /**
     * 录像文件上传后缀
     */
    private String videoSuffix;

    /**
     * 录像时间
     */
    private Integer videoRecordTime;

    /**
     * 截图文件上传路径
     */
    private String windowsImageUploadPath;
    private String linuxImageUploadPath;

    /**
     * 截图文件上传后缀
     */
    private String imageSuffix;

    /**
     * 截图张数
     */
    private Integer imageCount;

    /**
     * 萤石云 appKey appSecret
     */
    private String appKey;
    private String appSecret;

    /**
     * wvp配置
     */
    private WvpProConfig wvpPro;

    /**
     * 海康摄像头配置
     */
    // private HkSdkCfg hk;

    /**
     * 大华摄像头配置
     */
    // private DhSdkCfg dh;

    public String getImageUploadPath() {
        if (SystemUtil.getOsInfo().isWindows()) {
            return windowsImageUploadPath;
        }
        return linuxImageUploadPath;
    }

    public String getVideoUploadPath() {
        if (SystemUtil.getOsInfo().isWindows()) {
            return windowsVideoUploadPath;
        }
        return linuxVideoUploadPath;
    }

    /**
     * 喊话文件位置
     */
    private String windowsPcmPath;
    private String linuxPcmPath;

    public String getPcmPath(String fileName) {
        if (SystemUtil.getOsInfo().isWindows()) {
            return getDllPath() + windowsPcmPath + fileName;
        }
        return linuxPcmPath + fileName;
    }

    public String getDllPath() {
        String path = System.getProperty("user.dir").replaceAll("%20", " ");
        try {
            return java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
