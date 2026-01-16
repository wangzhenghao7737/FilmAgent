package com.xiaosa.filmagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * md文件切割
 * 从 cos获取md
 */
@Component
public class FilmMarkdownReader {

    public List<Document> loadMarkdown(Resource resource, Map<String, Object> metadata) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .withAdditionalMetadata(metadata)
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        return reader.get();
    }
}
