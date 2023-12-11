package com.moore.base.bean;

import lombok.Data;

/**
 * 分页对象
 *
 * @author moore
 */
@Data
public class PageParam {

    private int pageSize = 10;

    private int currentPage = 1;

}
