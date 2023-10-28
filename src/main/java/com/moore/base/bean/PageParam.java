package com.moore.base.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页对象
 *
 * @author moore
 */
@Data
@EqualsAndHashCode
public class PageParam {

    /**
     * 条数
     */
    private int pageSize = 10;

    /**
     * 当前页
     */
    private int currentPage = 1;

}
