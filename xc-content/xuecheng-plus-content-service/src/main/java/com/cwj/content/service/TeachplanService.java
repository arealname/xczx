package com.cwj.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.content.model.po.Teachplan;
import com.cwj.content.model.po.TeachplanMedia;
import com.cwj.content.model.po.dto.BindTeachplanMediaDto;
import com.cwj.content.model.po.dto.SaveTeachplanDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.xccommon.Result;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author cwj
 * @since 2024-12-04
 */
public interface TeachplanService extends IService<Teachplan> {

    List<TeachplanDto> tn(Long courseId);

    void su(SaveTeachplanDto teachplan);

    Result de(Long courseId);

    Result mv(Long planId, int i);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author Mr.M
     * @date 2022/9/14 22:20
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}
