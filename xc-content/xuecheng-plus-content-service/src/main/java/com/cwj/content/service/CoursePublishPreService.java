package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.CoursePublishPre;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface CoursePublishPreService extends IService<CoursePublishPre> {
    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    public void commitAudit(Long companyId,Long courseId);
}
