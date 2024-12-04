package com.xuecheng.xccommon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Long code;

    private Object data;

    public Result(String s){
        this.data=s;
    }

    public final static Result ok(String msg){
        return new Result(200L,msg);
    }

    public final static Result ok(Object o){
        return new Result(200L,o);

    }

    public final static Result fail(String err){
        return new Result(500L,err);
    }
}
