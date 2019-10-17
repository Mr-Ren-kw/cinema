package com.stylefeng.guns.rest.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    private String accessKeyId = "LTAI4Fx4zMyRQo5JpejCGySC";
    private String accessKeySecret = "jaFeRNf1LfwFjD90IuV6msamgxcng3";
    private String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    private String bucketName = "project3-cinema";
    private String imgPre = "http://project3-cinema.oss-cn-hangzhou.aliyuncs.com/";
    private String filePath = "/home/yyt/photo/";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImgPre() {
        return imgPre;
    }

    public void setImgPre(String imgPre) {
        this.imgPre = imgPre;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}