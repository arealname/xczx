package com.cwj.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.content.mapper.TeachplanMapper;
import com.cwj.content.mapper.TeachplanMediaMapper;
import com.cwj.content.model.po.Teachplan;
import com.cwj.content.model.po.TeachplanMedia;
import com.cwj.content.model.po.dto.BindTeachplanMediaDto;
import com.cwj.content.model.po.dto.SaveTeachplanDto;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.content.service.TeachplanService;
import com.cwj.xccommon.Result;
import com.cwj.xccommon.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> tn(Long courseId) {
        return teachplanMapper.getp(courseId);
    }

    @Override
    public void su(SaveTeachplanDto teachplan) {
        Long id = teachplan.getId();


        if (id == null) {
            Teachplan tp = new Teachplan();
            BeanUtils.copyProperties(teachplan, tp);
            int i = getorder(teachplan.getCourseId(), teachplan.getParentid());
            tp.setOrderby(i);
            teachplanMapper.insert(tp);
        } else {
            Teachplan tp = getById(id);
            BeanUtils.copyProperties(teachplan, tp);
            updateById(tp);
        }
    }

    @Override
    public Result de(Long planId) {

        //如果是小节可以直接
        Teachplan byId = getById(planId);
        if (byId.getGrade().equals(2)) removeById(planId);

        else {
            LambdaQueryWrapper<Teachplan> eq = new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getParentid, planId);
            int count = count(eq);
            if (count > 0) return Result.fail("有小节无法删除");
            else {
                removeById(planId);

            }
        }
        return Result.ok("删除成功");
    }

    @Override
    public Result mv(Long planId, int i) {  //上移或下移plan  ，即获取与自己同父亲的所有点

        Teachplan byId = getById(planId);
        Long parentid = byId.getParentid();
        Long courseId = byId.getCourseId();

        LambdaQueryWrapper<Teachplan> l = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Teachplan> eq = l.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid).orderByAsc(Teachplan::getOrderby);
        List<Teachplan> list = list(eq);

        int i1 = Collections.binarySearch(list, byId,
                Comparator.comparingInt(Teachplan::getOrderby)
        );

        if (i == 1) {    //上移

            if (i1 == 0) throw new ParamException("已经最顶层了");
            else {
                //交换与前一个的order
                int temp = list.get(i1 - 1).getOrderby();
                list.get(i1 - 1).setOrderby(list.get(i1).getOrderby());
                list.get(i1).setOrderby(temp);
                updateById(list.get(i1));
                updateById(list.get(i1 - 1));
            }
        } else {
            if (i1 == list.size()-1)throw new ParamException("已经最第层了");
            else {
                //交换与hou一个的order
                int temp = list.get(i1 + 1).getOrderby();
                list.get(i1 + 1).setOrderby(list.get(i1).getOrderby());
                list.get(i1).setOrderby(temp);
                updateById(list.get(i1));
                updateById(list.get(i1 + 1));
            }
        }
        return Result.ok(i == 1 ? "上移成功" : "下移成功");
    }

    private int getorder(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> l = new LambdaQueryWrapper<>();
        l.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid).orderByDesc(Teachplan::getOrderby);
        List<Teachplan> list = list(l);
        if (list.isEmpty()) return 1;
        return list.get(0).getOrderby() + 1;
    }


    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            throw new ParamException("错误");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            throw new ParamException("只允许第二级教学计划绑定媒资文件");

        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void rea(Long teachPlanId, Long mediaId) {
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachPlanId).eq(TeachplanMedia::getMediaId,mediaId));
    }


}
