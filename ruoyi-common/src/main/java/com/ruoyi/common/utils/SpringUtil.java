package com.ruoyi.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 * @author hewei
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    /**
     * 当前IOC
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 从当前IOC获取bean
     */
    public static <T> T getObject(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}