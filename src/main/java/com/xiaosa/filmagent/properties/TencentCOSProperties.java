package com.xiaosa.filmagent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云cos配置
 */
@ConfigurationProperties(prefix = "tencent.cos")
@Component
public class TencentCOSProperties {
    private String secretId;
    private String secretKey;
    private String bucket;
    private String region;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucketName) {
        this.bucket = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
