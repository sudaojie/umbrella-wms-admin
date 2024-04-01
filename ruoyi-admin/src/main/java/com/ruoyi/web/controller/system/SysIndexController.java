package com.ruoyi.web.controller.system;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController {
    /**
     * 系统基础配置
     */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index() {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }

    /**
     * 下载apk
     */
    @RequestMapping("/downLoadApk")
    public void downLoadApk(HttpServletResponse response) {
        try {
            File file=new File("/server/soft/umbrella-wms-admin/apk/lysk_v1.0.0.apk");
            //设置响应头，以附件的形式下载
            response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode("wms.apk","UTF-8"));
            //将目标文件复制一份，通过response以流的形式输出
            IOUtils.copy(new FileInputStream(file),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
