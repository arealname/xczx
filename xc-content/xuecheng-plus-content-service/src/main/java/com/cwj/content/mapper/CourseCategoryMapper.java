package com.cwj.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.content.model.po.CourseCategory;

import com.cwj.content.model.po.dto.CourseCategoryTreeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author cwj
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
