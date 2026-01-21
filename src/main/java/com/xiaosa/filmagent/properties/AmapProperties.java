package com.xiaosa.filmagent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置
 */
@ConfigurationProperties(prefix = "amap.weather")
@Component
public class AmapProperties {
    private String base;
    private String key;
    private String path;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
