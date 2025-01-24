package com.cwj.content.feign;


import com.cwj.content.config.MultipartSupportConfig;
import com.cwj.content.model.po.dto.UploadFileResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;




@FeignClient(value = "me-se", configuration = MultipartSupportConfig.class, fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaClient {

    @RequestMapping(value = "/media/upload/coursehtml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String uploadhtml(@RequestPart("filedata") MultipartFile filedata,
                              @RequestParam(value = "folder", required = false) String folder,
                              @RequestParam(value = "objectName", required = false) String objectName);
}
