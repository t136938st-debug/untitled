package org.example.common;

import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class CustomException extends RuntimeException {

    private final Integer code;

    public CustomException(String message) {
        super(message);
        this.code = 500;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
