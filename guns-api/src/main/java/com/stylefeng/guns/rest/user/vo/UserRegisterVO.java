package com.stylefeng.guns.rest.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterVO implements Serializable {

    private static final long serialVersionUID = 6498010154405078930L;

    private String username;
    private String password;
    private String email;
    private String mobile;
    private String address;
}
