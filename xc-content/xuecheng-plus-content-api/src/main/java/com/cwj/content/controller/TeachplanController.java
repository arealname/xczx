package com.cwj.content.controller;

import com.cwj.content.model.po.dto.BindTeachplanMediaDto;
import com.cwj.content.model.po.dto.SaveTeachplanDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.content.service.TeachplanService;
import com.cwj.xccommon.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程计划 前端控制器
 * </p>
 *
 * @author cwj
 */
@Slf4j
@RestController
@RequestMapping("teachplan")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;


    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.tn(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping()
    public Result saveTeachplan(@RequestBody SaveTeachplanDto teachplan){
        teachplanService.su(teachplan);
        return Result.ok("创建成功");
    }

    @DeleteMapping("/{planId}")
    public Result deleteplan(@PathVariable Long planId){
       return teachplanService.de(planId);
    }

    @PostMapping("/moveup/{planId}")
    public Result moveup(@PathVariable Long planId){
        return teachplanService.mv(planId,1);
    }

    @PostMapping("/movedown/{planId}")
    public Result movedown(@PathVariable Long planId){
        return teachplanService.mv(planId,2);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

}
