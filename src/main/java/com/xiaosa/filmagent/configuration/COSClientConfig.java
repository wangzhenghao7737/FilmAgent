package com.xiaosa.filmagent.configuration;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;
import com.xiaosa.filmagent.properties.TencentCOSProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class COSClientConfig {
    @Bean
    public COSClient cosClient(TencentCOSProperties properties) {
        BasicCOSCredentials cred = new BasicCOSCredentials(properties.getSecretId(), properties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(properties.getRegion()));
        return new COSClient(cred, clientConfig);
    }
}
