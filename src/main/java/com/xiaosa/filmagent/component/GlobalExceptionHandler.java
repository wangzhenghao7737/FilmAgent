package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import com.xiaosa.filmagent.exception.FilmException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FilmException.class)
    public ApiResponse<String> handleFilmException(FilmException e) {
        return ApiResponse.error(e.getFilmEnum());
    }
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(FilmEnum.SYSTEM_ERROR);
    }
}
