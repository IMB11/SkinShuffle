package dev.imb11.mineskin.exception;

import dev.imb11.mineskin.response.MineSkinResponse;

public class MineSkinRequestException extends RuntimeException {

    private final String code;
    private final MineSkinResponse<?> response;

    public MineSkinRequestException(String code, String message, MineSkinResponse<?> response) {
        super(message);
        this.code = code;
        this.response = response;
    }

    public MineSkinRequestException(String code, String message, MineSkinResponse<?> response, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.response = response;
    }

    public MineSkinResponse<?> getResponse() {
        return response;
    }
}
