package com.ruoyi.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建人
     */
    private static final String CREATE_USER = "createBy";
    /**
     * 创建时间
     */
    private static final String CREATE_DATE = "createTime";
    /**
     * 编辑人
     */
    private static final String EDIT_USER = "updateBy";
    /**
     * 编辑时间
     */
    private static final String EDIT_DATE = "updateTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (currentUser != null) {
            // 设置创建人&编辑人
            this.setFieldValByName(CREATE_USER, currentUser.getUser().getUserName(), metaObject);
            this.setFieldValByName(EDIT_USER, currentUser.getUser().getUserName(), metaObject);
        }else{
            // 设置创建人&编辑人
            this.setFieldValByName(CREATE_USER, "stackerOrAgvUserName", metaObject);
            this.setFieldValByName(EDIT_USER, "stackerOrAgvUserName", metaObject);
        }
        Date now = new Date();
        this.setFieldValByName(CREATE_DATE, now, metaObject);
        this.setFieldValByName(EDIT_DATE, now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (currentUser != null) {
            this.setFieldValByName(EDIT_USER, currentUser.getUser().getUserName(), metaObject);
        }
        this.setFieldValByName(EDIT_DATE, new Date(), metaObject);
    }

}
