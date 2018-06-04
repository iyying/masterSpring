package cn.spring.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc")//声明这个Controller处理的请求是什么
public class mvcController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }
}
