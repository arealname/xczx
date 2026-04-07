package com.xuecheng.media.api;


import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.xccommon.RestErrorResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.enu.FileEnum;
import com.xuecheng.media.model.po.MediaFiles;


import com.xuecheng.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFilesService mediaFilesService;

//    @PostMapping("upload/coursefile")
//    public UploadFileResultDto upl(@RequestBody MultipartFile filedata){
//        return new UploadFileResultDto();
//    }


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFilesService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
    }

    /**
     * @param filedata
     * @param folder
     * @param objectName
     * @return
     * @throws IOException 生成一个dto给服务
     */
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
                                      @RequestParam(value = "folder", required = false) String folder,
                                      @RequestParam(value = "objectName", required = false) String objectName) throws IOException {

        System.out.println("到达miclient1111........");
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();



        //文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());//文件名称

        //文件大小
        long fileSize = filedata.getSize();
        uploadFileParamsDto.setFileSize(fileSize);

        //创建临时文件
        File tempFile = File.createTempFile("minio", "temp");

        //上传的文件拷贝到临时文件
        filedata.transferTo(tempFile);

        //文件路径
        String absolutePath = tempFile.getAbsolutePath();

        //上传文件
        UploadFileResultDto uploadFileResultDto = mediaFilesService.uploadSmallFile(companyId, uploadFileParamsDto, absolutePath, folder, objectName);

        return uploadFileResultDto;
    }


    @RequestMapping(value = "/upload/coursehtml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String  uploadhtml(@RequestPart("filedata") MultipartFile filedata,
                              @RequestParam(value = "folder", required = false) String folder,
                              @RequestParam(value = "objectName", required = false) String objectName) {
        try {
            System.out.println("到达miclient222........");
            mediaFilesService.uploadHtml(objectName, filedata);
        } catch (Exception e) {
            System.out.println("exception:999:"+ e);
            return "exception:999:"+ e;
        }

        return "6666666";
    }


}
