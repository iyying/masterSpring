package com.gupaoedu.demo.service.impl;

import com.gupaoedu.demo.service.IDemoService;
import com.gupaoedu.mvcframework.webmvc.annotation.GPService;

@GPService
public class IDemoServiceImpl implements IDemoService {

    public String get(String name) {
        return "my name is " + name;
    }
}
