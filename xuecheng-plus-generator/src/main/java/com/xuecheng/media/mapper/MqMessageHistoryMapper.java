package com.xuecheng.media.mapper;

import com.xuecheng.media.model.po.MqMessageHistory;
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
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {

}
