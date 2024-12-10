package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.CourseBase;
import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.content.model.po.dto.AddCourseDto;
import com.cwj.content.model.po.dto.CourseBaseInfoDto;
import com.cwj.content.model.po.dto.QueryCourseParamsDto;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface CourseBaseService extends IService<CourseBase> {

    PageResult<CourseBase> list(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author Mr.M
     * @date 2022/9/7 17:51
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    CourseBaseInfoDto gid(Long courseId);

    CourseBaseInfoDto upd(CourseBaseInfoDto editCourseDto);
}
