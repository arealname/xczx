package com.cwj.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.content.model.po.Teachplan;
import com.cwj.content.model.po.dto.TeachplanDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<TeachplanDto> getp(Long courseId);
}
