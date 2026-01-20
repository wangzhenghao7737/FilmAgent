package com.xiaosa.filmagent.service;

import org.springframework.web.multipart.MultipartFile;

public interface RagService {
    boolean ingestDocument(MultipartFile file);
    boolean deleteDocument(String key);
    boolean existDocument(String key);
    void query();
}
