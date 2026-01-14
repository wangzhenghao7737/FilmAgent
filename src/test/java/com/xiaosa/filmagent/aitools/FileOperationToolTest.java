package com.xiaosa.filmagent.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    FileOperationTool tool = new FileOperationTool();
    @Test
    void readFile() {
        String s = tool.readFile("arknights.txt");
        System.out.println(s);
    }

    @Test
    void writeFile() {
        String s = tool.writeFile("arknights.txt", "hhhhhello world");
        System.out.println( s);
    }
}