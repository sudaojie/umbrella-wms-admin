package com.ruoyi.common.utils;
import com.lowagie.text.pdf.BaseFont;
import com.ruoyi.common.config.RuoYiConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * 生成pdf文件工具类
 * @author message丶小和尚
 * @create 2020/01/10
 */
@Slf4j
public class PdfUtil {

    private final static String DEFAULT_FONT = "/simsun.ttc";//默认字体资源文件（[宋体][simsun.ttc]）
    private final static String ENCODING = "UTF-8";//指定编码

    /**
     * 生成pdf
     * @param templateCode  模板完整路径
     * @param data  传入到freemarker模板里的数据（如果参数中有图片，对应参数只要图片的名称）
     * @param response  请求响应体
     */
//    public static void createPDF(String templateCode, Object data, OutputStream out, String imgPath) {
    public static void createPDF(String templateCode, Map data, HttpServletResponse response) {
        try {
            OutputStream out = response.getOutputStream();
            // 创建一个FreeMarker实例, 负责管理FreeMarker模板的Configuration实例
            Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            templateCode = templateCode.replaceAll("\\\\","/");
            String tempLatePath = templateCode.substring(0,templateCode.lastIndexOf("/")+1);
            // 指定FreeMarker模板文件的位置
            cfg.setDirectoryForTemplateLoading(new File(tempLatePath));
            ITextRenderer renderer = new ITextRenderer();
            String resource = RuoYiConfig.getUploadPath();
            // 设置 css中 的字体样式（暂时仅支持宋体和黑体）
            renderer.getFontResolver().addFont(resource + DEFAULT_FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            // 设置模板的编码格式
            cfg.setEncoding(Locale.CHINA, ENCODING);
            // 获取模板文件 template.ftl
            Template template = cfg.getTemplate(templateCode.replace(tempLatePath,""), ENCODING);
            StringWriter writer = new StringWriter();
            // 将数据输出到html中
            template.process(data, writer);
            writer.flush();
            String html = writer.toString();
            // 把html代码传入渲染器中
            renderer.setDocumentFromString(html);
            // 解决图片的相对路径问题 ##必须在设置document后再设置图片路径，不然不起作用
            // 如果使用绝对路径依然有问题，可以在路径前面加"file:/"
//            if(null != imgPath && !"".equals(imgPath)){
//                renderer.getSharedContext().setBaseURL("file:/" + imgPath);
//            }
//            if(null != imgName && !"".equals(imgName)){
//                renderer.getSharedContext().setBaseURL("file:" + IMG_BASE_PATH + imgName);
//            }
            renderer.layout();
            renderer.createPDF(out, false);
            renderer.finishPDF();
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("PDF导出异常", e);
        }
    }

}
