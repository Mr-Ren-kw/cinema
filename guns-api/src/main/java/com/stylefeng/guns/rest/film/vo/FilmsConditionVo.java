package com.stylefeng.guns.rest.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sld
 * @version 1.0
 * @date 2019/10/15 0:23
 */
@Data
public class FilmsConditionVo implements Serializable {

    private static final long serialVersionUID = -5461721721707341175L;
    private Integer showType;
    private Integer sortId;
    private Integer catId;
    private Integer sourceId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;
}
