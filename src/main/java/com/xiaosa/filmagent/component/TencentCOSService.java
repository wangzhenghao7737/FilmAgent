package com.xiaosa.filmagent.component;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.IOUtils;
import com.xiaosa.filmagent.properties.TencentCOSProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class TencentCOSService {
    private static final Logger log = LoggerFactory.getLogger(TencentCOSService.class);
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
        PutObjectRequest request = new PutObjectRequest(properties.getBucket()
                                    , key
                                    ,new File(filePath));
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
    public void updateVectorIdsMetadata(String key, List<String> vectorIds) {
        try {
            String bucket = properties.getBucket();

            // 构造 Copy 请求：源 = 目标（同对象）
            CopyObjectRequest copyReq = new CopyObjectRequest(bucket, key, bucket, key);

            // 获取原对象的元数据（可选：保留原有 user metadata）
            // 注意：COS 不支持增量更新 user metadata，必须全量覆盖
            ObjectMetadata newMetadata = new ObjectMetadata();

            // 设置新的自定义 metadata
            newMetadata.addUserMetadata("vector_ids", vectorIds.toString());

            // 重要：必须显式设置 contentType 等系统 metadata，否则会丢失！
            // 可以先 getMetadata 获取原值，或确保你知道类型
            // 这里我们先获取原 metadata
            ObjectMetadata originMeta = cosClient.getObjectMetadata(bucket, key);
            newMetadata.setContentType(originMeta.getContentType());
            newMetadata.setContentLength(originMeta.getContentLength());

            // 设置新 metadata
            copyReq.setNewObjectMetadata(newMetadata);

            // 执行复制（相当于“更新”metadata）
            cosClient.copyObject(copyReq);

            log.info("Successfully updated vector IDs metadata for key: {}", key);
        } catch (Exception e) {
            log.error("Error updating metadata via copyObject: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update metadata", e);
        }
    }


    public void addMetadata(String key, List<String> ids) {
        ObjectMetadata objectMetadata = cosClient.getObjectMetadata(properties.getBucket(), key);
        objectMetadata.addUserMetadata("vectore_ids", ids.toString());
//        objectMetadata.setHeader("x-cos-metadata-directive", "Replaced");
        objectMetadata.setHeader("x-cos-storage-class", "STANDARD_IA");
        objectMetadata.setContentType("text/plain"); // 否则可能被清空！
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(new Region(properties.getRegion()), properties.getBucket(), key, properties.getBucket(), key);
        copyObjectRequest.setNewObjectMetadata(objectMetadata);
        try {
            CopyObjectResult copyObjectResult = cosClient.copyObject(copyObjectRequest);
            log.info("Update COS metadata success: {}", copyObjectResult.getRequestId());
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
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
