package com.cwj.content.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwj.content.mapper.CourseBaseMapper;
import com.cwj.content.mapper.CourseTeacherMapper;
import com.cwj.content.mapper.TeachplanMapper;
import com.cwj.content.model.po.CourseBase;
import com.cwj.content.model.po.CourseTeacher;
import com.cwj.content.service.CourseTeacherService;
import com.cwj.xccommon.Result;
import com.cwj.xccommon.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 课程-教师关系表 前端控制器
 * </p>
 *
 * @author cwj
 */
@Slf4j
@RestController
@RequestMapping("courseTeacher")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @GetMapping("list/{courseId}")
    public List<CourseTeacher> gettl(@PathVariable Long courseId) {
        return courseTeacherService.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
    }

    @PostMapping()
    public void upt(@RequestBody CourseTeacher ct) {
        Long comp_id = 1232141425L;
        Long courseId = ct.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectOne(new LambdaQueryWrapper<CourseBase>().eq(CourseBase::getId, courseId));
        System.out.println(courseBase.getCompanyId());
        if (!courseBase.getCompanyId().equals(comp_id))
            throw new ParamException("这个老师不属于你们机构，无法修改");
        if (ct.getId() != null) courseTeacherMapper.updateById(ct);
        else courseTeacherMapper.insert(ct);
    }

    @DeleteMapping("/course/{cid}/{tid}")
    public void det(@PathVariable(value = "cid") Long cid, @PathVariable(value = "tid") Long tid) {
        Long comp_id = 1232141425L;
        CourseBase courseBase = courseBaseMapper.selectOne(new LambdaQueryWrapper<CourseBase>().eq(CourseBase::getId, cid));
        if (!courseBase.getCompanyId().equals(comp_id))
            throw new ParamException("这个老师不属于你们机构，无法删除");
        courseTeacherMapper.deleteById(tid);
    }


}
