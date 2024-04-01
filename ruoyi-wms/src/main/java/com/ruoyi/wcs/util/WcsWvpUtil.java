package com.ruoyi.wcs.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SpringUtil;
import com.ruoyi.wcs.config.CameraConfig;
import com.ruoyi.wcs.config.WvpProConfig;
import com.ruoyi.wcs.enums.camera.WcsWvpApiEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * wvp-pro 工具类
 *
 * @author xutianbao
 * @date 2023/3/2 15:28
 **/
@Slf4j
@Component
public class WcsWvpUtil {

    public static final String redisTokenKey = "wvp-pro:token";
    private static RedisCache redisCache;

    @Autowired
    public void setRedisTemplate(RedisCache redisCache) {
        WcsWvpUtil.redisCache = redisCache;
    }

    /**
     * 单例
     **/
    private WcsWvpUtil() {
    }

    public static WcsWvpUtil getInstance() {
        return SingletonHandler.instance.singleton;
    }

    private enum SingletonHandler {
        // 枚举项，每个枚举项只会创建一个
        instance;
        private final WcsWvpUtil singleton;

        SingletonHandler() {
            singleton = new WcsWvpUtil();
        }
    }

    /**
     * 获取wvp-pro系统登录token
     **/
    public String getLoginToken() {
        if (redisCache != null && !redisCache.hasKey(redisTokenKey)) {
            CameraConfig cameraConfig = SpringUtil.getObject(CameraConfig.class);
            WvpProConfig wvpPro = cameraConfig.getWvpPro();
            Map<String, Object> paramMap = new HashMap<>(2);
            paramMap.put("username", wvpPro.getUserName());
            paramMap.put("password", wvpPro.getPassword());
            String result = HttpUtil.get(wvpPro.getUrl() + WcsWvpApiEnum.GET_LOGIN_TOKEN.getCode(), paramMap);
            // 返回值
            JSONObject resultJson = JSONUtil.parseObj(result);
            String code = String.valueOf(resultJson.get("code"));
            if (WcsWvpApiEnum.SUCCESS.getCode().equals(code)) {
                JSONObject dataJson = JSONUtil.parseObj(resultJson.get("data"));
                String token = String.valueOf(dataJson.get("accessToken"));
                redisCache.setCacheObject(redisTokenKey, token);
                redisCache.expire(redisTokenKey, Integer.parseInt(WcsWvpApiEnum.expirationTime.getCode()));
            }
        }

        return ObjectUtil.isNotNull(redisCache.getCacheObject(redisTokenKey)) ? redisCache.getCacheObject(redisTokenKey).toString() : "";
    }


}
