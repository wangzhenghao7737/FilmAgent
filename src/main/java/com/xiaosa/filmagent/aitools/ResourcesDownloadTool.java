package com.xiaosa.filmagent.aitools;

import cn.hutool.http.HttpUtil;
import com.xiaosa.filmagent.constant.FilmConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ResourcesDownloadTool {
    private static final String DOWNLOAD_DIR = FilmConstant.DOWNLOAD_PATH;

    @Tool(description = "Downloads a file from the given URL and saves it with the name")
    public String download(@ToolParam(description = "URL of the file to download") String url,
                           @ToolParam(description = "Name of the file to save(Name should include extension name like apple.png or message.txt)") String name) {
        try {
            Path dir = Paths.get(DOWNLOAD_DIR);
            if(!Files.exists(dir)){
                Files.createDirectory(dir);
            }
            HttpUtil.downloadFile(url, new File(DOWNLOAD_DIR+"/"+name));
            return "File downloaded successfully to " + DOWNLOAD_DIR + "/" + name;
        } catch (Exception e) {
            return "Error downloading file: " + e.getMessage();
        }
    }
}
