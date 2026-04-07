package com.cwj.content.job;


import com.alibaba.fastjson.JSON;
import com.cwj.content.config.MultipartSupportConfig;
import com.cwj.content.feign.MediaClient;
import com.cwj.content.mapper.CoursePublishMapper;
import com.cwj.content.mapper.CoursePublishPreMapper;
import com.cwj.content.model.po.CoursePublish;
import com.cwj.content.model.po.CoursePublishPre;
import com.cwj.content.model.po.dto.CoursePreviewDto;
import com.cwj.content.model.po.dto.UploadFileResultDto;
import com.cwj.content.service.CoursePublishPreService;
import com.cwj.content.service.CoursePublishService;
import com.cwj.message.po.MqMessage;
import com.cwj.message.service.MqAbstractClass;
import com.cwj.message.service.MqMessageService;
import com.cwj.xccommon.exception.ParamException;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Component
public class CourseJob extends MqAbstractClass {

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    CoursePublishMapper coursePublishPreMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public boolean execute(MqMessage msg) {

        String messageType = msg.getMessageType();

        if (!messageType.equals("course_publish")) return false;

        Long l = Long.valueOf(msg.getBusinessKey1());  //课程id


        //下面2步都完成才叫完成


        saveredis(msg, l);
        System.out.println("缓存成功");

        try {
            savehtml(msg,l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        System.out.println("静态化完成");

        return true;
    }


    public void saveredis(MqMessage mqMessage, Long cid) {

        //检查消息是否已经处理过，幂等性处理
        Long mid=mqMessage.getId();

        int stageOne = mqMessageService.getStageOne(mid);

            if(stageOne<1){
                log.debug("开始执行第一阶段任务");
                int i = mqMessageService.completedStageOne(mid);
                if(i>0){
                    log.debug("完成第一阶段任务");
                }

            }else{
                log.debug("无需执行第一阶段任务");
                return;
            }

        CoursePublish coursePublishPre = coursePublishPreMapper.selectById(cid);

        System.out.println(coursePublishPre);
        String jsonString = JSON.toJSONString(coursePublishPre);
        System.out.println(jsonString);
        redisTemplate.opsForValue().set(cid.toString(), jsonString);
    }

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    MediaClient mediaClient;

    public void savehtml(MqMessage mqMessage,Long cid) throws IOException, TemplateException {


        //检查消息是否已经处理过，幂等性处理

        Long mid=mqMessage.getId();
        int stageTwo = mqMessageService.getStageTwo(mid);

            if(stageTwo<1){
                log.debug("开始执行第二阶段任务");
                int i = mqMessageService.completedStageTwo(mid);
                if(i>0){
                    log.debug("完成第二阶段任务");
                }

            }else{
                log.debug("无需执行第二阶段任务");
                return;
            }


        //配置freemarker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_19);

        //加载模板
        //选指定模板路径,classpath下templates下
        String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符编码
        configuration.setDefaultEncoding("utf-8");

        //指定模板文件名称
        Template template = configuration.getTemplate("course_template.ftl");

        //准备数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(cid);

        Map<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);

        //静态化
        //参数1：模板，参数2：数据模型
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        //将静态化内容输出到文件中
        InputStream inputStream = IOUtils.toInputStream(content);


        File tempFile = File.createTempFile("course" + UUID.randomUUID(), ".html");
        //输出流
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        IOUtils.copy(inputStream, outputStream);

        String html1 = mediaClient.uploadhtml(convertFile(tempFile), "html", cid + ".html");

        System.out.println(html1);

        System.out.println("存储为html");
    }

    public MultipartFile convertFile(File f) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(f);
        if (multipartFile == null) throw new ParamException("转换文件失败");

        return multipartFile;
    }

    @XxlJob("CoursePublishJobHandler")
    public void cphandler() {

        String jobParam = XxlJobHelper.getJobParam();

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);
        log.info("开始执行第" + shardIndex + "批任务");

        process(shardIndex, shardTotal, "course_publish", 5);
    }
}
