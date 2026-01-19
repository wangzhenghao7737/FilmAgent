package com.xiaosa.filmagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * 读取内容：当前前使用临时文件
 * 如果markdown内容很规范，只有#标题，可以传递字符串和手动切分
 */
@Component
public class FilmMarkdownReader {

    public List<Document> loadMarkdown(MultipartFile file, Map<String, Object> metadata) throws IOException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("film_md_", ".md");
        try {
            // 写入内容
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            // 使用 FileSystemResource
            Resource resource = new FileSystemResource(tempFile.toFile());

            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata(metadata)
                    .build();

            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
            return reader.get();
        }catch (IOException e){
            throw new RuntimeException("处理文件时出错", e);
        }
        finally {
            // 尝试删除临时文件（reader.get() 已读完，可安全删除）
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
                // 忽略删除失败
            }
        }
    }
}
