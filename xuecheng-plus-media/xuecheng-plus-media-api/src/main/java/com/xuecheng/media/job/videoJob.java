package com.xuecheng.media.job;


import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class videoJob {

    @XxlJob("videoJobHandler")
    public void vhandler() {
        for (int i = 0; i < 5; i++) System.out.println(i);
    }


    @XxlJob("shardingJobHandler")
    public void shardingtest() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);
        log.info("开始执行第"+shardIndex+"批任务");
    }


}
