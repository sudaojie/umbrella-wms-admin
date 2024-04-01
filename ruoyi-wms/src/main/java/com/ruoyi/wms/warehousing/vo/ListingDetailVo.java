package com.ruoyi.wms.warehousing.vo;

import com.ruoyi.wms.warehousing.domain.ListingDetail;
import lombok.Data;

/**
 * @author hewei
 * @date 2023/4/17 0017 16:56
 */
@Data
public class ListingDetailVo extends ListingDetail {

    /**
     * 货物类别
     */
    private String categoryName;

    /**
     * 托盘上货物数量
     */
    private Long goodsNum;

}
