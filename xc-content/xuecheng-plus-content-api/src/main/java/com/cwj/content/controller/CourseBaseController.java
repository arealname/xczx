package com.cwj.content.controller;

import com.cwj.content.model.po.CourseBase;
import com.cwj.content.service.CourseBaseService;
import com.xuecheng.xccommon.PageParams;
import com.xuecheng.xccommon.PageResult;
import com.xuecheng.xccommon.dto.QueryCourseParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程基本信息 前端控制器
 * </p>
 *
 * @author cwj
 */
@Slf4j
@RestController
@RequestMapping("courseBase")
public class CourseBaseController {

    @Autowired
    private CourseBaseService  courseBaseService;

    @PostMapping("/course/list")  //分页参数，课程条件
    public PageResult<CourseBase> getcourselist(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.list(pageParams,queryCourseParamsDto);
    }


}
