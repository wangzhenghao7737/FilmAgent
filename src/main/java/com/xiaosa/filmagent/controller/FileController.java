package com.xiaosa.filmagent.controller;

import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import com.xiaosa.filmagent.service.RagService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private RagService ragService;
    /**
     * 上传文件到腾讯云COS对象存储
     */
    @PostMapping("/upload")
    public ApiResponse<Boolean> uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return ApiResponse.error(FilmEnum.FAIL_UPLOAD_FILE);
        }
        if(file.isEmpty() ||
                !(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).equals("md"))){
            return ApiResponse.error(FilmEnum.FILE_UPLOAD_TYPE_ERROR);
        }
        ragService.ingestDocument(file);
        return ApiResponse.success(true);
    }
    // 删除文件
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteFile(@RequestParam("key") String key) {
        if (StringUtils.hasText( key)){
            if(ragService.existDocument( key)){
                return ApiResponse.success(ragService.deleteDocument( key));
            }
        }
        return ApiResponse.success(false);
    }
}
