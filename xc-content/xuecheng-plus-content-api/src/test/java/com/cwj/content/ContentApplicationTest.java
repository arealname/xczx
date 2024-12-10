package com.cwj.content;

import com.cwj.content.mapper.TeachplanMapper;
import com.cwj.content.model.po.dto.TeachplanDto;
import com.cwj.content.service.impl.TeachplanServiceImpl;
import com.cwj.xccommon.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ContentApplicationTest {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanServiceImpl teachplanService;
    @Test
    void t(){
        List<TeachplanDto> getp = teachplanMapper.getp(87l);
        System.out.println(getp.get(0).getPname());
    }

    @Test
    void test2(){
        Result mv = teachplanService.mv(315l, 1);
    }

}
