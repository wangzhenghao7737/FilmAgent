package com.xiaosa.filmagent.constant;

public interface FilmConstant {
    String CHAT_MEMORY_PATH = System.getProperty("user.dir") + "/tmp/chat_memory";
    String DOWNLOAD_PATH = System.getProperty("user.dir") + "/tmp/download";
    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp/file";
    String PDF_SAVE_DIR = System.getProperty("user.dir") + "/tmp/pdf";
    // advisor order
    int FILM_LOGGER_ADVISOR_ORDER = 100;

    int FILM_DEFAULT_CHAT_MEMORY_SIZE = 10;
}
