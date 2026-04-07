package com.cwj.xccommon.exception;


import com.cwj.xccommon.RestErrorResponse;
import com.cwj.xccommon.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice     //AOP

public class GlobalExceptionHandler{
    @ResponseBody
    @ExceptionHandler(ParamException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse handleParamerror(ParamException e){
        System.out.println("发生自定义错误ParamEx:"+e);
        return new RestErrorResponse(e.getErrorInfo());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse handleerror(Exception e){

        System.out.println("发生RuntimeError:"+e);
        return new RestErrorResponse(e.getMessage());
    }

}
