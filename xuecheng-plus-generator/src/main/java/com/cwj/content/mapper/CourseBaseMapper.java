package com.cwj.content.mapper;

import com.cwj.content.model.po.CourseBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

}
