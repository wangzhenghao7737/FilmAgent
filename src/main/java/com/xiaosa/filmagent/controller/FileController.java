package com.xiaosa.filmagent.controller;

import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import com.xiaosa.filmagent.service.RagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private RagService ragService;
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
}
