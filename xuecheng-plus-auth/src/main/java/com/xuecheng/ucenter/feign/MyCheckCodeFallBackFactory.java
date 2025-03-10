package com.xuecheng.ucenter.feign;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class MyCheckCodeFallBackFactory implements FallbackFactory<CheckCodeClient> {

    @Override
    public CheckCodeClient create(Throwable throwable) {
        return (key, code) -> {
            System.out.println("验证码服务错误，熔断降级"+throwable.getMessage());
            return false;
        };
    }
}
