package com.hailang.config.exception;

import com.hailang.config.utils.Result;
import com.hailang.config.utils.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return ResultUtils.failed(e.getMessage());
    }
}
