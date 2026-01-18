package org.sxi.vo;

import org.sxi.biz.BizException;

public class Result {

    private int code;
    private String message;
    private Object data;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static Result ok(String message, Object data) {
        return new Result(1, message, data);
    }
    public static Result ok(String message) {
        return new Result(1, message);
    }

    public static Result error(String message) {
        throw new BizException(message);
    }


}
