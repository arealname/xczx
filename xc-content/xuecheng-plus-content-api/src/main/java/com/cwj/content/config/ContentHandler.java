package com.cwj.content.config;

//import ch.qos.logback.core.ContextBase;
import com.cwj.content.model.po.CourseBase;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@CanalTable("course_base")
public class ContentHandler implements EntryHandler<CourseBase> {
    @Override
    public void insert(CourseBase courseBase) { //监听插入数据
        EntryHandler.super.insert(courseBase);
        System.out.println(courseBase);
    }

    @Override
    public void update(CourseBase before, CourseBase after) {
        System.out.println(before.toString());
        System.out.println(after.toString());
        EntryHandler.super.update(before, after);
    }

    @Override
    public void delete(CourseBase courseBase) {
        EntryHandler.super.delete(courseBase);
    }
}
