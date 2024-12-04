package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseCategoryMapper;
import com.cwj.content.model.po.CourseCategory;
import com.cwj.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

}
