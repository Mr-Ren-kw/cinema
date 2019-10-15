package com.stylefeng.guns.rest.user.validator;

import java.io.Serializable;

/**
 * 验证的凭据
 *
 * @author fengshuonan
 * @date 2017-08-27 13:27
 */
public interface Credence extends Serializable {

    /**
     * 凭据名称
     */
    String getCredenceName();

    /**
     * 密码或者是其他的验证码之类的
     */
    String getCredenceCode();
}
