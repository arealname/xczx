package com.cwj;


import io.minio.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@EnableConfigurationProperties(MinioProperties.class)
@Import(MinioConfig.class)
public class MinioServiceImpl implements MinioUtil {

    @Autowired
    MinioProperties minioProperties;

    @Autowired
    MinioClient minioClient;

    private final static String sep = "/";

    @Override
    public String buildpath(String prefix, String filename) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String format = simpleDateFormat.format(new Date());
        StringBuilder sb=new StringBuilder();
        sb.append(sep);
        sb.append(prefix);
        sb.append(sep);
        sb.append(format+sep+filename);
        return sb.toString();

    }

    @Override
    public String uploadImgFile(String prefix, String filename, InputStream inputStream) { //上传图片,需要前缀，文件名，输入流

        String buildpath = buildpath(prefix, filename);

        try {
            PutObjectArgs build1 = PutObjectArgs.builder().bucket(minioProperties.getMinioBucket())
                    .stream(inputStream, inputStream.available(), -1)
                    .object(buildpath)
                    .contentType("image/jpg").build();
            minioClient.putObject(build1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder(minioProperties.getMinioDomain());
        sb.append(buildpath);
        return sb.toString();

    }

    @Override
    public String uploadHtmlFile(String prefix, String filename, InputStream inputStream) {
        String buildpath =prefix+"/"+ filename;

        try {
            PutObjectArgs build1 = PutObjectArgs.builder().bucket(minioProperties.getMinioBucket())
                    .object(buildpath)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("text/html").build();
            minioClient.putObject(build1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder(minioProperties.getMinioDomain());
        sb.append(buildpath);
        return sb.toString();
    }

    @Override
    public void delete(String pathUrl) {
        try {
            RemoveObjectArgs build = RemoveObjectArgs.builder().bucket(minioProperties.getMinioBucket()).object(pathUrl).build();
            minioClient.removeObject(build);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public String downLoadFile(String pathUrl, String tar) {

        DownloadObjectArgs build = DownloadObjectArgs.builder().bucket(minioProperties.getMinioBucket()).object(pathUrl).filename(tar).build();
        try {
            minioClient.downloadObject(build);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return tar;
    }

    @Override
    public File downLoadFileByStream(String pathUrl) {
        GetObjectArgs build = GetObjectArgs.builder().bucket(minioProperties.getMinioBucket()).object(pathUrl).build();

        FileOutputStream fout=null;
        File temp=null;
        try {
            InputStream inputStream = minioClient.getObject(build);
            temp = File.createTempFile("minio" + UUID.randomUUID(), ".tmp");
            fout = new FileOutputStream(temp);
            byte[] b=new byte[1024];
            int l=-1;
            while((l=inputStream.read(b))!=-1){
                fout.write(b,0,l);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if(fout!=null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return temp;
    }


}
