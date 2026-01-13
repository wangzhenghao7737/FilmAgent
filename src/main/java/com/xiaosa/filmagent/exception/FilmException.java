package com.xiaosa.filmagent.exception;

import com.xiaosa.filmagent.entity.FilmEnum;
import lombok.Getter;

@Getter
public class FilmException extends RuntimeException{
    private final FilmEnum filmEnum;
    public FilmException(FilmEnum filmEnum) {
        super(filmEnum.getMessage());
        this.filmEnum = filmEnum;
    }
}
