package com.stylefeng.guns.rest.film.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class FilmConditionVo implements Serializable {

    private static final long serialVersionUID = -619190901274357493L;
    private Integer showType;
    private Integer sortId;
    private Integer catId;
    private Integer sourceId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;
}
