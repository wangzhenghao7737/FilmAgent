package com.xiaosa.filmagent.entity;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private Long timestamp;
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    public static <T> ApiResponse<T> success(){
        return new ApiResponse<T>(FilmEnum.SUCCESS.getCode(),
                FilmEnum.SUCCESS.getMessage(), null);
    }
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<T>(FilmEnum.SUCCESS.getCode(),
                FilmEnum.SUCCESS.getMessage(), data);
    }
    public static <T> ApiResponse<T> error(BaseEnum baseEnum){
        return new ApiResponse<T>(baseEnum.getCode(),
                baseEnum.getMessage(), null);
    }
}
