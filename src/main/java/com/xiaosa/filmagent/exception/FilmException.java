package com.xiaosa.filmagent.exception;

import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import lombok.Getter;

public class FilmException extends RuntimeException{
    private final FilmEnum filmEnum;
    public FilmException(FilmEnum filmEnum) {
        super(filmEnum.getMessage());
        this.filmEnum = filmEnum;
    }

    public FilmEnum getFilmEnum() {
        return filmEnum;
    }
}
