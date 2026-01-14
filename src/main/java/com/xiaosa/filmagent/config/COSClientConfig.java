package com.xiaosa.filmagent.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class COSClientConfig {
//    @Bean
    public COSClient cosClient(@Value("${tencent.oos.secret-id}") String secretId
                               ,@Value("${tencent.oos.secret-key}") String secretKey) {
        BasicCOSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region("ap-guangzhou"));
        return new COSClient(cred, clientConfig);
    }
}
