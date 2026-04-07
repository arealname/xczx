package com.cwj.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.config.MultipartSupportConfig;
import com.cwj.content.feign.MediaClient;
import com.cwj.content.mapper.CourseBaseMapper;
import com.cwj.content.mapper.CoursePublishMapper;
import com.cwj.content.mapper.CoursePublishPreMapper;
import com.cwj.content.model.po.CourseBase;
import com.cwj.content.model.po.CoursePublish;
import com.cwj.content.model.po.CoursePublishPre;
import com.cwj.content.model.po.dto.CourseBaseInfoDto;
import com.cwj.content.model.po.dto.CoursePreviewDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.content.service.CourseBaseService;
import com.cwj.content.service.CoursePublishService;
import com.cwj.content.service.TeachplanService;
import com.cwj.message.po.MqMessage;
import com.cwj.message.service.MqMessageService;
import com.cwj.xccommon.exception.ParamException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {
    @Autowired
    CourseBaseService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.gid(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.tn(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }


    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;


    @Transactional
    @Override
    public void publishCourse(Long companyId, Long courseId) {


        /**
         * 1、向课程发布表course_publish插入一条记录,记录来源于课程预发布表，如果存在则更新，发布状态为：已发布。
         * 2、更新course_base表的课程发布状态为：已发布
         * 3、删除课程预发布表的对应记录。
         * 4、向mq_message消息表插入一条消息，消息类型为：course_publish
         * 约束：
         * 1、课程审核通过方可发布。
         * 2、本机构只允许发布本机构的课程。
         */


        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);


        if (!companyId.equals(courseBaseInfo.getCompanyId())) throw new ParamException("公司不符");

        String auditStatus = courseBaseInfo.getAuditStatus();
        if (!auditStatus.equals("202004")) throw new ParamException("未通过审核");

        saveCoursePublish(courseId);  //保存课程发布信息,包括课程基本信息、课程发布状态、课程计划等信息


        saveMsg(courseId);  //保存消息到消息表，供后续发送消息使用


        int i = coursePublishPreMapper.deleteById(courseId);




    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish;
    }

    @Autowired
    MqMessageService mqMessageService;


    private void saveCoursePublish(Long courseId) {
        //整合课程发布信息
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            throw new ParamException("课程预发布信息不存在");
        }

        CoursePublish coursePublish = new CoursePublish();

        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }


    public void saveMsg(Long courseId) {
        MqMessage coursePublish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (coursePublish == null) {
            throw new ParamException("生成发布消息失败");
        }
    }
}
