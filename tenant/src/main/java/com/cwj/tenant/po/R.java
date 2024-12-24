package com.cwj.tenant.po;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class R {


    String errorCode="200";
    String errorMessage="success";

    Boolean success=true;
    @JSONField(serialize = true)
    Object data;

    public static R ok(Object _data){
        R r = new R();
        r.setData(_data);
        return r;
    }

    public static R fail(String msg){
        R r=new R();
        r.setSuccess(false);
        r.setErrorCode("500");
        r.setErrorMessage(msg);
        return r;
    }

}
