package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cwj.MinioUtil;
import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.xccommon.RestResponse;
import com.cwj.xccommon.exception.ParamException;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.sun.media.jfxmediaimpl.MediaUtils;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Value("${cwj.minio.minioBucket}")
    private String bu;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }


    public String gettype(String originalFilename) {
        if (originalFilename == null)
            originalFilename = "";

        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(substring);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    private String getmd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Autowired
    MinioUtil minioUtil;

    @Autowired
    MediaFileService mediaFileServiceProxy;

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String absolutePath, String folder, String objectName) {

        File file = new File(absolutePath);
        if (!file.exists()) throw new ParamException("本地文件丢失");

        if (StringUtils.isEmpty(folder)) folder = "defaultFolder";

        String filename = uploadFileParamsDto.getFilename();
        String gettype = gettype(filename);
        String md5 = getmd5(file);
        String ex = filename.substring(filename.lastIndexOf("."));

        StringBuilder res = new StringBuilder();
        res.append(folder).append("/").append(md5).append(ex);
        String miniofilename = res.toString();

        String p = "";
        String tp = "001001";
        if (gettype.startsWith("video")) {
            p = "video";
            tp = "001002";
        } else if (gettype.startsWith("image")) {
            p = "pic";
            tp = "001001";
        } else {
            p = "other";
            tp = "001003";
        }
        uploadFileParamsDto.setFileType(tp);
        try {
            minioUtil.uploadImgFile(p, miniofilename, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }



        MediaFiles mediaFiles = null;
        try {
            mediaFiles = mediaFileServiceProxy.updateDb(companyId, p, md5, uploadFileParamsDto, miniofilename);
        } catch (Exception e) {
            System.out.println(e);
        }

        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        uploadFileResultDto.setUrl("/"+bu+"/"+uploadFileResultDto.getUrl());
        return uploadFileResultDto;

    }


    @Override
    public String uploadHtml(String fname, MultipartFile f) {
        try {
            System.out.println(88888);
            minioUtil.uploadHtmlFile("course",fname,f.getInputStream());
        } catch (Exception e) {
            System.out.println("发生错误");
            throw new RuntimeException(e);
        }
        return "666";
    }






    @Transactional
    @Override
    public MediaFiles updateDb(Long companyId, String prefix, String md5, UploadFileParamsDto uploadFileParamsDto, String miniofilename) {
        System.out.println(this);
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);

            mediaFiles.setFileId(md5);
            mediaFiles.setId(md5);
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bu);
            mediaFiles.setStatus("1");



            mediaFiles.setUrl(miniofilename);

            mediaFiles.setCreateDate(LocalDateTime.now());
            int insert = mediaFilesMapper.insert(mediaFiles);
//            int i=1/0;
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
                throw new ParamException("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Autowired
    MinioClient minioClient;

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String ckpath= fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + chunkIndex;

        GetObjectArgs mediafiles = GetObjectArgs.builder().bucket("mediafiles").object(ckpath).build();
        InputStream object = null;
        try {
            object = minioClient.getObject(mediafiles);
//            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return RestResponse.success(object != null);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chuckIndex, String fp) {

        File f=new File(fp);
        String folder = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        PutObjectArgs p = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            p = PutObjectArgs.builder().bucket("mediafiles")
                    .contentType("video/mp4").stream(fileInputStream, f.length(), -1)
                    .object(folder + chuckIndex)
                    .build();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            minioClient.putObject(p);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return RestResponse.success(false);
        }

        return RestResponse.success(true);
    }

    @Autowired
    MediaProcessMapper mediaProcessMapper;


    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        String folder = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));

        String targetpath=folder+fileMd5+extName;


        System.out.println(targetpath);
        System.out.println(chunkTotal);

        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket("mediafiles")
                        .object(folder.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket("mediafiles")
                            .object(targetpath)
                            .sources(sourceObjectList)
                            .build());
            System.out.println("合并文件成功:{}"+targetpath);
        } catch (Exception e) {
            System.out.println("合并文件成功:{}"+fileMd5+e.getMessage()+e);
//            log.debug("合并文件失败,fileMd5:{},异常:{}",);
            return RestResponse.validfail(false, "合并文件失败。");
        }

        Long b = valifyFile(targetpath, fileMd5);
        if(b!=-1) {
            removechunk(fileMd5,chunkTotal);

            uploadFileParamsDto.setFileSize(b);

            String filename = uploadFileParamsDto.getFilename();
            String gettype = gettype(filename);
            String p = "";
            String tp = "001001";
            if (gettype.startsWith("video")) {
                p = "video";
                tp = "001002";
            } else if (gettype.startsWith("image")) {
                p = "pic";
                tp = "001001";
            } else {
                p = "other";
                tp = "001003";
            }

            uploadFileParamsDto.setFileType(tp);
            addProcessing(fileMd5,fileName,uploadFileParamsDto,"mediafiles",targetpath);
            updateDb(companyId,p,fileMd5,uploadFileParamsDto,"/mediafiles/"+targetpath);
            System.out.println("合并完成");
        }
        else System.out.println("合并失败");
        return RestResponse.success(b);

    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    private void addProcessing(String fileMd5, String fileName, UploadFileParamsDto uploadFileParamsDto, String bu,String p) {
        //将新上传的大文件加入待处理数据表中

        MediaProcess mediaProcess = new MediaProcess();
        mediaProcess.setFileId(fileMd5);
        mediaProcess.setFilename(fileName);
        mediaProcess.setBucket(bu);
        mediaProcess.setFilePath(p);
        mediaProcess.setUrl(p);
        mediaProcess.setCreateDate(LocalDateTime.now());
        mediaProcess.setStatus("1");
        mediaProcess.setFailCount(0);


        mediaProcessMapper.insert(mediaProcess);
    }

    public Long valifyFile(String mpath,String md5){
        File f= downLoadfromminio(mpath);

        try {
            FileInputStream fileInputStream = new FileInputStream(f);

            String s = DigestUtils.md5Hex(fileInputStream);

            return s.equals(md5)?f.length():-1;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File downLoadfromminio(String p){

        return minioUtil.downLoadFileByStream(p);
    }

    public void removechunk(String fileMd5,int total){
        String chunkFileFolderPath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(total)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("mediafiles").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}",deleteError.objectName(),e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}",chunkFileFolderPath,e);
        }
    }
}
