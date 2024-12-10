package com.cwj.xccommon.en;

public enum ErrorEnum {
    Error_PARAM(6001,"参数错误"),
    Error_NET(6002,"网络错误"),

    Error_UNKNOWN(6003,"未知错误");

    private int code;
    private String msg;

   ErrorEnum(int _code,String _msg){
        code=_code;msg=_msg;
    };



    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }


}
