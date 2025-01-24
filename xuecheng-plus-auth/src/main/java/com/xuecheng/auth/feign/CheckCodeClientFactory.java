package com.xuecheng.auth.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeFeign> {
    @Override
    public CheckCodeFeign create(Throwable throwable) {
        return new CheckCodeFeign() {
            @Override
            public Boolean verify(String key, String code) {
                log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
                return null;
            }
        };
    }
}
