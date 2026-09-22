package com.b2cmall.common;
public class BizException extends RuntimeException {
    public final int status;
    public BizException(int status, String message) { super(message); this.status=status; }
}
