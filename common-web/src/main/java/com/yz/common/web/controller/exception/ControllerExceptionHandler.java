package com.yz.common.web.controller.exception;

import com.alibaba.fastjson.JSON;
import com.yz.common.core.exception.HandlerException;
import com.yz.common.core.http.ResponseMessage;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @author yangzhao
 * @Description
 * @Date create by 20:57 18/2/25
 */
@ControllerAdvice
@Component
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String handle(Exception exception) {
        logger.error("错误信息  ====  "+exception.getMessage());
        if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            List<FieldError> fieldErrors = bindException.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                return JSON.toJSONString(ResponseMessage.error(10010,fieldError.getField() + fieldError.getDefaultMessage()));
            }
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) exception;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> violation : violations) {
                return JSON.toJSONString(ResponseMessage.error(10010,violation.getPropertyPath() + violation.getMessage()));
            }
        }
        if (exception instanceof HandlerException) {
            HandlerException handlerException = (HandlerException) exception;
            return JSON.toJSONString(new ResponseMessage(handlerException.getCode(), handlerException.getErrorInfo()));
        }
        if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) exception;
            return JSON.toJSONString(ResponseMessage.error(10010, "请求参数" + missingServletRequestParameterException.getParameterName() + "不能为空"));
        }
        if (exception instanceof FileUploadException) {
            FileUploadException fileUploadException = (FileUploadException) exception;
            return JSON.toJSONString(ResponseMessage.error(10010, fileUploadException.getMessage()));
        }

        return JSON.toJSONString(ResponseMessage.error(0, "服务异常"));
    }
}
