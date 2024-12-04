package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.CourseTeacherMapper;
import com.cwj.content.model.po.CourseTeacher;
import com.cwj.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

}
