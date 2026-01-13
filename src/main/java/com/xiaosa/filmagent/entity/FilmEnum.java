package com.xiaosa.filmagent.entity;

public enum FilmEnum implements BaseEnum{
    SUCCESS(200,"Success","Success"),
    SYSTEM_TIMEOUT(501,"System time out","System time out"),
    SYSTEM_BUSY(502,"System busy","System busy"),
    SYSTEM_ERROR(503,"System error","System error"),;
    private final int code;
    private final String message;
    private final String description;
    FilmEnum(int code, String message, String description){
        this.code = code;
        this.message = message;
        this.description = description;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
