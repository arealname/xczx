package com.cwj.xccommon.exception;

import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.en.ErrorEnum;
import org.apache.commons.lang3.StringUtils;

public class ParamException extends RuntimeException {


    private ErrorEnum errorEnum;
    private String msg;


    public ParamException(ErrorEnum en) {
        errorEnum = en;
    }

    public ParamException(String s){
        msg=s;
    }
    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public String getErrorInfo(){
        if(errorEnum!=null)return errorEnum.getMsg();
        else if(StringUtils.isNotBlank(msg))return msg;
        else return "未知错误";
    }

    public static void cast(String errMessage){
        throw new ParamException(errMessage);
    }


}
