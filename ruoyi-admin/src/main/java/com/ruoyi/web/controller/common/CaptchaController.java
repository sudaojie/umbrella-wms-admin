package com.ruoyi.web.controller.common;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.CaptchaType;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.service.ISysConfigService;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController {

    @Autowired
    private RedisCache redisCache;

    // 验证码类型
    @Value("${ruoyi.captchaType}")
    private String captchaType;

    @Autowired
    private ISysConfigService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return ajax;
        }
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        // 生成验证码
        Captcha captcha = switchCaptcha(captchaType);
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == CaptchaType.arithmetic.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        redisCache.setCacheObject(verifyKey, captchaValue, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        ajax.put("uuid", uuid);
        ajax.put("img", captcha.toBase64());
        return ajax;
    }

    /**
     * 依据配置信息生产验证码
     *
     * @param captchaType 验证码类型
     * @return /
     */
    private Captcha switchCaptcha(String captchaType) {
        Integer width = 111;
        Integer height = 36;
        Integer length = 2;
        Captcha captcha;
        synchronized (this) {
            switch (captchaType) {
                case "math":
                    // 算术类型 https://gitee.com/whvse/EasyCaptcha
                    captcha = new ArithmeticCaptcha(width, height);
                    // 几位数运算，默认是两位
                    captcha.setLen(length);
                    break;
                case "char":
                    captcha = new ChineseCaptcha(width, height);
                    captcha.setLen(length);
                    break;
                case "chinese_gif":
                    captcha = new ChineseGifCaptcha(width, height);
                    captcha.setLen(length);
                    break;
                case "gif":
                    captcha = new GifCaptcha(width, height);
                    captcha.setLen(length);
                    break;
                case "spec":
                    captcha = new SpecCaptcha(width, height);
                    captcha.setLen(length);
                    break;
                default:
                    throw new BaseException("验证码配置信息错误！");
            }
        }
        return captcha;
    }
}
