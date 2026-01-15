package com.xiaosa.filmagent.component;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.xiaosa.filmagent.properties.TencentCOSProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Date;

@Component
public class TencentCOSService {
    private COSClient cosClient;
    private TencentCOSProperties properties;
    public TencentCOSService(COSClient cosClient, TencentCOSProperties properties) {
        this.cosClient = cosClient;
        this.properties = properties;
    }
    public String putObject(String filePath) {
        String key = filePath.substring(filePath.lastIndexOf(".") + 1)
                        +"/"
                        +filePath.substring(filePath.lastIndexOf("/") + 1);
        PutObjectRequest request = new PutObjectRequest(properties.getBucket(), key, new java.io.File(filePath));
        try {
            PutObjectResult result = cosClient.putObject(request);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getObjectTemporaryUrl(String key) {
        if(!objectExists(key)){
            return null;
        }
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(properties.getBucket(), key);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60));
        request.setMethod(HttpMethodName.GET);
        URL objectUrl = cosClient.generatePresignedUrl(request);
        return objectUrl.toString();
    }

    public void deleteObject(String key) {
        if(objectExists(key)){
            cosClient.deleteObject(properties.getBucket(), key);
        }
    }
    public boolean objectExists(String key) {
        if(StringUtils.hasText(key)){
            return cosClient.doesObjectExist(properties.getBucket(), key);
        }
        return false;
    }
}
