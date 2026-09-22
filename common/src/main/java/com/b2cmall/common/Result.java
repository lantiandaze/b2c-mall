package com.b2cmall.common;
public class Result<T> {
    public int status;
    public String message;
    public T data;
    public Result() {}
    public Result(int status, String message, T data) { this.status=status; this.message=message; this.data=data; }
    public static <T> Result<T> success(T data) { return new Result<>(200, "成功", data); }
}
