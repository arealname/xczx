package com.cwj.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cwj.message.mapper.MqMessageHistoryMapper;
import com.cwj.message.po.MqMessageHistory;
import com.cwj.message.service.MqMessageHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
