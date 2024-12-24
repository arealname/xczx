package com.cwj.tenant.controller;


import com.alibaba.fastjson.JSONObject;
import com.cwj.tenant.po.R;
import com.cwj.tenant.po.RequestParam;
import com.cwj.tenant.service.T1212Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cwj
 */
@Slf4j
@RestController
@RequestMapping("t1212")
public class T1212Controller {

    private final T1212Service t1212Service;


    @Autowired
    public T1212Controller(T1212Service t1212Service) {
        this.t1212Service = t1212Service;
    }

    @PostMapping("/pro/map")
    public R getmap(@RequestBody RequestParam requestParam) {
        return t1212Service.getmap(requestParam);

    }
}
