package com.xiaosa.filmagent.component;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.utils.IOUtils;
import com.xiaosa.filmagent.properties.TencentCOSProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        PutObjectRequest request = new PutObjectRequest(properties.getBucket(), key, new File(filePath));
        try {
            cosClient.putObject(request);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //用于前端预览或下载
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
    //以string格式获取md文件
    public Resource downloadMarkdownAsResource(String key) {
        if (!objectExists(key)) {
            throw new IllegalArgumentException("COS object not found: " + key);
        }
        GetObjectRequest request = new GetObjectRequest(properties.getBucket(), key);
        COSObject object = cosClient.getObject(request);

        try (InputStream is = object.getObjectContent()) {
            byte[] content = is.readAllBytes();
            return new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return key;
                }

                @Override
                public long contentLength() {
                    return content.length;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to read COS object: " + key, e);
        }
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
