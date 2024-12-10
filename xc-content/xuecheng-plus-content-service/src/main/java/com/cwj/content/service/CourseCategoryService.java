package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.CourseCategory;
import com.cwj.content.model.po.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface CourseCategoryService extends IService<CourseCategory> {


    List<CourseCategoryTreeDto> tn(String number);
}
