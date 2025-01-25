package com.xuecheng.messagesdk.service.impl;

import com.xuecheng.messagesdk.model.po.MqMessageHistory;
import com.xuecheng.messagesdk.mapper.MqMessageHistoryMapper;
import com.xuecheng.messagesdk.service.MqMessageHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory> implements MqMessageHistoryService {

}
