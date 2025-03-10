package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.cwj.xccommon.exception.ParamException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import com.xuecheng.learning.config.PayResultConfig;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.learning.service.PayResultNotifyService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayResultNotifyServiceImpl implements PayResultNotifyService {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Override
    @RabbitListener(queues = PayResultConfig.PAYNOTIFY_QUEUE)
    public void receivepaynotify(Message message, Channel channel) {


        System.out.println("收到消息了吗。。。。。。。。。。。。。");


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        //获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(), MqMessage.class);
        System.out.println(String.format("学习中心服务接收支付结果:%s",mqMessage));


        String messageType = mqMessage.getMessageType();
        String businessKey2 = mqMessage.getBusinessKey2();

        //这里只处理支付结果通知
        if (PayResultConfig.MESSAGE_TYPE.equals(messageType) && "60201".equals(businessKey2)) {
            //选课记录id
            String choosecourseId = mqMessage.getBusinessKey1();
            //添加选课
            boolean b = myCourseTablesService.saveChooseCourseSuccess(choosecourseId);
            if (!b) {
                //添加选课失败，抛出异常，消息重回队列
                throw new ParamException("收到支付结果，添加选课失败");

            }
        }
    }
}
