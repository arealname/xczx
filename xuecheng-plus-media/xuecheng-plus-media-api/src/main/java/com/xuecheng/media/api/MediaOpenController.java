package com.xuecheng.media.api;

import com.cwj.xccommon.RestResponse;
import com.cwj.xccommon.exception.ParamException;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFilesService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFilesService mediaFilesService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {

        MediaFiles mediaFiles = mediaFilesService.getFileById(mediaId);
        if (mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())) {
            throw new ParamException("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());

    }


}