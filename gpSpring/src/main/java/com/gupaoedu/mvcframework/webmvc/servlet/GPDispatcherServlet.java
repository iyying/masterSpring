package com.gupaoedu.mvcframework.webmvc.servlet;

import com.gupaoedu.mvcframework.webmvc.annotation.GPAutowired;
import com.gupaoedu.mvcframework.webmvc.annotation.GPController;
import com.gupaoedu.mvcframework.webmvc.annotation.GPService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class GPDispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<String>();
    private Map<String, Object> ioc = new HashMap<String, Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doDispatch();
    }

    private void doDispatch(){

    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("======GPSpring is init===========");

        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        doScanner(contextConfig.getProperty("scanPackage"));
        doInstance();
        doAutowired();
        initHandlerMapping();
    }

    private void initHandlerMapping() {

        if (ioc.isEmpty()) { return; }

        // 处理Controller
        for (Map.Entry<String,Object> entry : ioc.entrySet()) {


        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) { return; }
        for (Map.Entry<String,Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if(!field.isAnnotationPresent(GPAutowired.class)) { continue; }

                GPAutowired autowired = field.getAnnotation(GPAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)) {
                    beanName = field.getType().getName();
                }

                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }



            }
        }


    }

    private void doInstance() {
        if (classNames.isEmpty()){ return; }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                //有注解的类才初始化
                if (clazz.isAnnotationPresent(GPController.class)) {
                    Object obj = clazz.newInstance();
                    // 初始化以后需要放到IOC容器中
                    // key默认是类名的首字母小写
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,obj);

                } else if (clazz.isAnnotationPresent(GPService.class)){
                    // 1、默认首字母小写
                    // 2、接口要是把实现类赋值
                    // 3、如果自定义，优先用自定义的名字

                    GPService service = clazz.getAnnotation(GPService.class);
                    String beanName = service.value();

                    if("".equals(beanName.trim())) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();

                    ioc.put(beanName, instance);

                    // 解决了子类引用赋值给父类的问题
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            } else {
                String className = scanPackage + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
