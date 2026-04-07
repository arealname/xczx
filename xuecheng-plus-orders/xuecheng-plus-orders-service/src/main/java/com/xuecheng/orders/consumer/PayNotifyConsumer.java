package com.xuecheng.orders.consumer;

import com.rabbitmq.client.Channel;
import com.xuecheng.orders.config.PayMessageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class PayNotifyConsumer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = PayMessageConfig.PAYNOTIFY_QUEUE)
    public void handleMessage(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            // 模拟业务处理
            processBusiness(msg);

            // 手动 ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("消费失败，消息: {}", msg, e);

            // 获取已重试次数
            Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get("x-retry-count");
            if (retryCount == null) {
                retryCount = 0;
            }

            if (retryCount < 3) {
                retryCount++;
                log.warn("消息消费失败，重试第 {} 次: {}", retryCount, msg);

                // 重新构造消息，带上重试次数
                Message newMsg = MessageBuilder
                        .withBody(message.getBody())
                        .copyHeaders(message.getMessageProperties().getHeaders())
                        .setHeader("x-retry-count", retryCount)
                        .build();

                // 发送到延迟队列（例如延迟 10 秒后重试）
                rabbitTemplate.convertAndSend(
                        PayMessageConfig.DELAY_EXCHANGE, 
                        PayMessageConfig.DELAY_ROUTING_KEY, 
                        newMsg
                );

            } else {
                log.error("消息消费失败超过 3 次，进入人工审核队列: {}", msg);

                // 转人工审核队列
                rabbitTemplate.convertAndSend(
                        PayMessageConfig.MANUAL_EXCHANGE,
                        PayMessageConfig.MANUAL_ROUTING_KEY,
                        message
                );
            }

            // 拒绝当前消息，避免卡住队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private void processBusiness(String msg) {
        // 模拟业务逻辑
        if (msg.contains("fail")) {
            throw new RuntimeException("模拟业务异常");
        }
        log.info("消费成功，处理消息: {}", msg);
    }
}
