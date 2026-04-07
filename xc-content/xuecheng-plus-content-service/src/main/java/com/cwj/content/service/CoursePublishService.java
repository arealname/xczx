package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.CoursePublish;
import com.cwj.content.model.po.dto.CoursePreviewDto;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface CoursePublishService extends IService<CoursePublish> {

    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    public void publishCourse(Long companyId, Long courseId);

    CoursePublish getCoursePublish(Long courseId);
}
