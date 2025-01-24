package com.cwj.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cwj.message.po.MqMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {
    @Select("SELECT t.* FROM mq_message t WHERE t.id % #{shardTotal} = #{shardindex} and t.state='0' and t.message_type=#{messageType} limit #{count}")
    List<MqMessage> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("messageType") String messageType, @Param("count") int count);
}
