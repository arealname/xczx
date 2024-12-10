package com.cwj.content.controller;

import com.cwj.content.model.po.CourseBase;
import com.cwj.content.service.CourseBaseService;
import com.cwj.xccommon.PageParams;
import com.cwj.xccommon.PageResult;
import com.cwj.content.model.po.dto.AddCourseDto;
import com.cwj.content.model.po.dto.CourseBaseInfoDto;
import com.cwj.content.model.po.dto.QueryCourseParamsDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
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
@RequestMapping("/")
public class CourseBaseController {

    @Autowired
    private CourseBaseService  courseBaseService;

    @PostMapping("/course/list")  //分页参数，课程条件
    public PageResult<CourseBase> getcourselist(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.list(pageParams,queryCourseParamsDto);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseService.gid(courseId);
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody CourseBaseInfoDto editCourseDto){
        return courseBaseService.upd(editCourseDto);
    }



}
