package com.gupaoedu.demo.mvc.action;

import com.gupaoedu.demo.service.IDemoService;
import com.gupaoedu.mvcframework.webmvc.annotation.GPAutowired;
import com.gupaoedu.mvcframework.webmvc.annotation.GPController;
import com.gupaoedu.mvcframework.webmvc.annotation.GPRequestMapping;
import com.gupaoedu.mvcframework.webmvc.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@GPController
@GPRequestMapping("/demo")
public class DemoAction {

    @GPAutowired
    private IDemoService demoService;

    @GPRequestMapping("/query.json")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @GPRequestParam("name") String name){
        String result = demoService.get(name);
        try{
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GPRequestMapping("/add.json")
    public void add(){

    }

    @GPRequestMapping("/remove.json")
    public void remove(){

    }
}
