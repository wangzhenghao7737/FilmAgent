package com.xiaosa.filmagent.component;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.IOUtils;
import com.qcloud.cos.model.Tag.Tag;
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
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

@Component
public class TencentCOSService {
    private static final Logger log = LoggerFactory.getLogger(TencentCOSService.class);
    private COSClient cosClient;
    private TencentCOSProperties properties;
    public TencentCOSService(COSClient cosClient, TencentCOSProperties properties) {
        this.cosClient = cosClient;
        this.properties = properties;
    }
    public String putObject(InputStream is,String key,String file_id,List<String> ids) {
        ObjectMetadata userMetadata = new ObjectMetadata();
        userMetadata.addUserMetadata("file-id", file_id);
        PutObjectRequest request = new PutObjectRequest(properties.getBucket()
                                    , key
                                    , is
                                    , userMetadata);
        request.setMetadata(userMetadata);
        try {
            PutObjectResult putObjectResult = cosClient.putObject(request);
            // cos标签拓展用法
            // todo 标签长度
            /**
             * 标签键长度范围为1-127个字符（采用 UTF-8 格式）
             * 标签值长度范围为1-255个字符 （采用 UTF-8 格式）
             * 一个对象最多10个不同的对象标签
             */
            List<Tag> tags = new ArrayList<Tag>();
            Tag tag = new Tag("vectore_ids",String.join(",", ids));
            tags.add(tag);
            SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(properties.getBucket(), key, new ObjectTagging(tags));
            cosClient.setObjectTagging(setObjectTaggingRequest);
            return putObjectResult.getRequestId();
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

    //获取元数据
    public String getObjectMetadata(String key) {
        // todo 异常处理
        ObjectMetadata metadata = cosClient.getObjectMetadata(properties.getBucket(), key);
        Map<String, String> userMetadata = metadata.getUserMetadata();
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        final Date lastModified = metadata.getLastModified();
        final String crc64Ecma = metadata.getCrc64Ecma();

        return metadata.getUserMetadata().get("file-id");
    }
    //获取标签
    public String getObjectTags(String key) {
        GetObjectTaggingRequest getObjectTaggingRequest = new GetObjectTaggingRequest(properties.getBucket(), key);
        GetObjectTaggingResult getObjectTaggingResult = cosClient.getObjectTagging(getObjectTaggingRequest);
        List<Tag> resultTagSet = getObjectTaggingResult.getTagSet();
        // todo 转化为List<String> milvus_id
        return resultTagSet.get(0).getValue();
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
