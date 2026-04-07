package com.cwj.message.service;

import com.cwj.message.po.MqMessage;
import com.cwj.message.service.MqMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public abstract class MqAbstractClass {

    public abstract boolean execute(MqMessage msg);  //执行消息的接口，后续实现类实现具体

    @Autowired
    MqMessageService mqMessageService;

    public void process(int shardIndex, int shardTotal, String messageType, int count) {

        try {

            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal, messageType, count);

            System.out.println(messageList.get(0));

            int i = messageList.size();

            System.out.println(i);
            System.out.println("取出待处理消息" + i + "条");
            if (i <= 0) {
                return;
            }
            ExecutorService executorService = Executors.newFixedThreadPool(i);


            CountDownLatch countDownLatch = new CountDownLatch(i);
            messageList.forEach(message -> {
                executorService.execute(() -> {
                    System.out.println("开始任务:{}" + message);

                    //处理任务
                    try {
                        boolean result = execute(message);
                        if (result) {
                            System.out.println(message);
                            //更新任务状态,删除消息表记录,添加到历史表
                            int completed = mqMessageService.completed(message.getId());
                            if (completed > 0) {
                                log.debug("任务执行成功:{}", message);
                            } else {
                                log.debug("任务执行失败:{}", message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.debug("任务出现异常:{},任务:{}", e.getMessage(), message);
                    }
                    //计数
                    countDownLatch.countDown();
                    log.debug("结束任务:{}", message);
                });
            });


            //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
            countDownLatch.await(30, TimeUnit.SECONDS);
            System.out.println("结束....");
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }


}
