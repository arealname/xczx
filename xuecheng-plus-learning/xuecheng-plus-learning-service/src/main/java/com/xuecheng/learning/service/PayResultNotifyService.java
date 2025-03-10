package com.xuecheng.learning.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

public interface PayResultNotifyService {
    public void receivepaynotify(Message message, Channel channel);
}
