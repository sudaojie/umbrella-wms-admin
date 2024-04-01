package com.ruoyi.wcs.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author hewei
 * @date 2023/4/10 0010 14:29
 */
@Data
@Accessors(chain = true)
public class WcsTreeVo {

    /**
     * 编号
     */
    private String id;

    /**
     * 标签
     */
    private String label;

    /**
     * 子节点
     */
    private List<WcsTreeVo> children;

    /**
     * 类型
     */
    private String type;

}
