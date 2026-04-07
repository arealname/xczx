package com.xuecheng.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.xccommon.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFilesService extends IService<MediaFiles> {


    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


    public MediaFiles AddToDb(Long companyId, String md5, UploadFileParamsDto uploadFileParamsDto, String miniofilename);


    public RestResponse<Boolean> checkFile(String fileMd5);
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
    //上传分块
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chuckIndex,String path);

    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);




    MediaFiles getFileById(String mediaId);

    public String uploadHtml(String name, MultipartFile f);

    UploadFileResultDto uploadSmallFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String absolutePath,
                                        String folder, String objectName);

    RestResponse uploadBigFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, MultipartFile file);
}
