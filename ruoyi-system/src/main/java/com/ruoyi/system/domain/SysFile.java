package com.ruoyi.system.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServerConfigUtil;
import com.ruoyi.common.utils.file.FileUploadUtils;

import java.io.IOException;

/**
 * 系统附件对象
 *
 * @author yangjie
 * @date 2022-10-28
 */
@TableName("sys_file")
public class SysFile extends BaseEntity {


    /**
     * 编号
     */
    private String id;

    /**
     * 业务编号
     */
    private String buinessId;

    /**
     * 类型
     */
    @Excel(name = "类型")
    private String type;

    /**
     * 源文件名称
     */
    @Excel(name = "源文件名称")
    private String displayName;

    /**
     * 文件名称
     */
    @Excel(name = "文件名称")
    private String name;

    /**
     * 路径
     */
    @JsonIgnore
    @Excel(name = "路径")
    private String path;

    /**
     * 大小
     */
    @Excel(name = "大小")
    private Long size;

    /**
     * 排序值
     */
    @Excel(name = "排序值")
    private Long orderNo;

    /**
     * 附件访问路径
     */
    @TableField(exist = false)
    private String visitUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuinessId() {
        return buinessId;
    }

    public void setBuinessId(String buinessId) {
        this.buinessId = buinessId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getVisitUrl() {
        if (StrUtil.isEmpty(getPath())) {
            return "";
        }
        try {
            String pathFileName = FileUploadUtils.getVisitUrlForPath(RuoYiConfig.getUploadPath(), this.getPath());
            return pathFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    public void setVisitUrl(String visitUrl) {
        this.visitUrl = visitUrl;
    }
}
