package com.cwj.content.mapper;

import com.cwj.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

}
