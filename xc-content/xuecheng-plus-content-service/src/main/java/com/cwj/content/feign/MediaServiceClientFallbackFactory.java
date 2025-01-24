package com.cwj.content.feign;

import com.cwj.content.model.po.dto.UploadFileResultDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable throwable) {
        return new MediaClient(){
            @Override
            public String uploadhtml(MultipartFile upload, String folder, String objectName) {
                //降级方法
                System.out.println("调用媒资管理服务上传文件时发生熔断，异常信息:{}"+throwable.toString()+throwable);
                return "null";
            }
        };
    }
}