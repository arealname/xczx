package com.cwj.content.controller;

import com.cwj.content.service.CourseCategoryService;
import com.cwj.content.model.po.dto.CourseCategoryTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 课程分类 前端控制器
 * </p>
 *
 * @author cwj
 */
@Slf4j
@RestController
@RequestMapping("course-category")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService  courseCategoryService;

    @GetMapping("/tree-nodes")
    public List<CourseCategoryTreeDto> treenodes(){
        List<CourseCategoryTreeDto> tn = courseCategoryService.tn("1");
        return tn;
    }
}
