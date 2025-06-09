package com.elm.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(ResultCode.ERROR.getCode(), message);
    }

    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message);
    }
} 