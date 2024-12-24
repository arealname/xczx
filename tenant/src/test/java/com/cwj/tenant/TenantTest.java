package com.cwj.tenant;

import com.cwj.tenant.mapper.T1212Mapper;
import com.cwj.tenant.po.R;
import com.cwj.tenant.po.RequestParam;
import com.cwj.tenant.po.T1212;
import com.cwj.tenant.service.T1212Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TenantTest {

    @Autowired
    T1212Service t1212Service;
    @Test
    void test(){
        R getmap = t1212Service.getmap(new RequestParam("1", "2024-Q3", "1541718963002540032", "1", "2021-Q1"));
        System.out.println(getmap);

    }

}