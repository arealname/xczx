package com.xuecheng.media.job;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwj.MinioUtil;
import com.xuecheng.Mp4VideoUtil;
import com.xuecheng.VideoUtil;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.analysis.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Component
public class VideoProcess {

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaProcessService mediaFileProcessService;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @XxlJob("videohandler")
    public void videohr() {


        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();
        List<MediaProcess> list = null;


        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);
        log.info("开始执行第" + shardIndex + "批任务");

        int size = 0;

        try {

            int i = Runtime.getRuntime().availableProcessors();

            list = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, i);

            size = list.size();

            log.info("取出到的待处理视频任务数:{}", size);

            if (size <= 0) {
                log.info("当前分片没有任务需要处理");
                return;
            }
        } catch (Exception e) {
            log.error("获取待处理视频任务失败:{}", e.getMessage());
            return;
        }


        ExecutorService executorService = Executors.newFixedThreadPool(size);

        CountDownLatch latch = new CountDownLatch(size);

        list.forEach(mediaProcess -> {
            executorService.execute(() -> {
                try {
                    pvideo(mediaProcess);
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", mediaProcess.getFileId(), mediaProcess.getUrl(), null);

                } finally {
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), mediaProcess.getUrl(), null);
                    latch.countDown();
                }

            });
        });

    }

    String ffmpeg_path = "D:\\LiulanqiDownload\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe";//ffmpeg的安装位置
    //源avi视频的路径

    String mp4folder_path = "D://";

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioUtil minioUtil;

    public void pvideo(MediaProcess mp) {
        String url = mp.getUrl();   //下载源文件

        File file = minioUtil.downLoadFileByStream(url);

        String targetname = "minio" + UUID.randomUUID() + mp.getFilePath().substring(mp.getFilePath().lastIndexOf("."));

        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, file.getAbsolutePath(), targetname, mp4folder_path);
        mp4VideoUtil.generateMp4();  //生成的lujing

        File f = new File(mp4folder_path + targetname);

        try {
            String fileMd5 = mp.getFileId();
            String fp = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
            String p = fp + fileMd5 + mp.getFilePath().substring(mp.getFilePath().lastIndexOf("."));

            System.out.println(p);
            PutObjectArgs mediafiles = PutObjectArgs.builder().bucket("mediafiles")
                    .object(p).stream(new FileInputStream(f), f.length(), -1)
                    .contentType("video/mp4").build();
            minioClient.putObject(mediafiles);


//            try{
//                RemoveObjectArgs rmediafiles = RemoveObjectArgs.builder().bucket("mediafiles").object(url).build();
//                minioClient.removeObject(rmediafiles);
//            }catch (Exception e){
//                System.out.println(e);
//            }


        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }

}
