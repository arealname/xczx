package com.xuecheng.media.api;

import com.cwj.xccommon.RestResponse;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 大文件上传接口
 * @date 2022/9/6 11:29
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {


    @Autowired
    MediaFilesService mediaFilesService;

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return mediaFilesService.checkFile(fileMd5);
    }


    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) throws Exception {
        return mediaFilesService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {

        File templocal=File.createTempFile("minio",".temp");
        file.transferTo(templocal);
        String filepath=templocal.getAbsolutePath();
        return mediaFilesService.uploadChunk(fileMd5,chunk, filepath);
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {

        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);
        return mediaFilesService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);

    }


    @ApiOperation(value = "上传大文件")
    @PostMapping("/upload/bigfile")
    public RestResponse uploadMultiThread(@RequestBody  MultipartFile file
                                    ) throws Exception {

        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(file.getOriginalFilename());


        return mediaFilesService.uploadBigFile(companyId,uploadFileParamsDto,file);

    }


}