package com.b2cmall.shop.web;

import com.b2cmall.common.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.stream.Collectors;
@RestControllerAdvice
public class ApiExceptionHandler extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> business(BizException e) {
        return ResponseEntity.status(e.status).body(new Result<>(e.status,e.getMessage(),null));
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, org.springframework.web.context.request.WebRequest request) {
        String message=e.getBindingResult().getAllErrors().stream().map(x->x.getDefaultMessage()).sorted().collect(Collectors.joining("；"));
        return ResponseEntity.badRequest().body(new Result<>(400,message,null));
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, org.springframework.web.context.request.WebRequest request) {
        return ResponseEntity.badRequest().body(new Result<>(400,"请求 JSON 格式不正确",null));
    }
    // Preserve Spring MVC statuses and headers (e.g. Allow for 405).
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers,
            HttpStatus status, org.springframework.web.context.request.WebRequest request) {
        return new ResponseEntity<>(new Result<>(status.value(),status.getReasonPhrase(),null),headers,status);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> unexpected(Exception e) {
        org.slf4j.LoggerFactory.getLogger(getClass()).error("Request failed: {}",e.getClass().getSimpleName());
        return ResponseEntity.status(500).body(new Result<>(500,"服务内部错误",null));
    }
}
