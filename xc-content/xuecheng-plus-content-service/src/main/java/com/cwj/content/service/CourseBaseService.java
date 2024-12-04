package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.CourseBase;
import com.xuecheng.xccommon.PageParams;
import com.xuecheng.xccommon.PageResult;
import com.xuecheng.xccommon.dto.QueryCourseParamsDto;

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
}
