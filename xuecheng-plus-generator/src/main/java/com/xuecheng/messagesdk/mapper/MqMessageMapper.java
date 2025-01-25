package com.xuecheng.messagesdk.mapper;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {

}
