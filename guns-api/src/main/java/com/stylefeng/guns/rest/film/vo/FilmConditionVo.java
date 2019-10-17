package com.stylefeng.guns.rest.film.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class FilmConditionVo implements Serializable {

    private static final long serialVersionUID = -42821626195120137L;
    private Integer showType;
    private Integer sortId;
    private Integer catId;
    private Integer sourceId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;
    private Integer offset;

    public Integer getOffset() {
        return offset;
    }
}
