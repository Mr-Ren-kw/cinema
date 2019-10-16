package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class Banner implements Serializable {

    private static final long serialVersionUID = -7704127142769584440L;
    private String bannerId;
    private String bannerUrl;
    private String bannerAddress;

}
