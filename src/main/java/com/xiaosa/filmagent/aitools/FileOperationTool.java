package com.xiaosa.filmagent.aitools;

import com.xiaosa.filmagent.constant.FilmConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件操作工具
 * 默认使用TXT格式
 * todo 1.扩展格式 2.oos对象存储
 */
@Component
public class FileOperationTool {
    private static final String FILE_PATH = FilmConstant.FILE_SAVE_DIR;
    public FileOperationTool() {
        Path path = Paths.get(FILE_PATH);
        if(!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Tool(description = "Reads a file and returns its content")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName){
        String filePath = FILE_PATH+"/"+fileName;
        try {
            return Files.readString(Paths.get(filePath));
        }catch (Exception e){
            return "Error reading file: " + e.getMessage();
        }
    }
    @Tool(description = "Writes a file with the given content. You should inform the users of the storage location of the files(The default file format is TXT).")
    public String writeFile(@ToolParam(description = "Name of a file to write") String fileName,
                            @ToolParam(description = "Content to write into the file") String content){
        Path path = Paths.get(FILE_PATH+"/"+fileName);
        try {
            if(Files.exists(path)){
                Files.delete(path);
            }
            Files.createFile(path);
            Files.writeString(path, content);
            return "File written successfully to "+FILE_PATH+"/"+fileName;
        }catch (Exception e){
            return  "Error writing file: " + e.getMessage();
        }
    }
}
