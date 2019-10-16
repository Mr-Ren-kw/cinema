package com.stylefeng.guns.rest.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = -1478077428695572570L;

    /**
     * 主键编号
     */
    private Integer uuid;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户性别 0-男，1-女
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户住址
     */
    private String address;
    /**
     * 头像URL
     */
    private String headAddress;
    /**
     * 个人介绍
     */
    private String biography;
    /**
     * 生活状态 0-单身，1-热恋中，2-已婚，3-为人父母
     */
    private Integer lifeState;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;


}
