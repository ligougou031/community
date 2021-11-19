package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

@Controller
@RequestMapping(path = "/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @ResponseBody//将字符串以json格式返回给浏览器
    @RequestMapping(path = "/hello")
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @ResponseBody
    @RequestMapping(path = "/find")
    public String find() {
        return alphaService.find();
    }

    @RequestMapping(path = "/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET请求
     */

    // /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "5") int limit){

        System.out.println(current);
        System.out.println(limit);

        return "some students";
    }

    // /students/123
    @RequestMapping(path = "/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudentsById(@PathVariable(name = "id") int id){

        System.out.println(id);

        return "a student by id";
    }


    /**
     * POST请求
     */


    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(@RequestParam(name = "name") String name,
                              @RequestParam(name = "age") int age){
        System.out.println(name);
        System.out.println(age);
        return "save success";
    }

    /**
     * 响应html数据
     */

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","李狗狗");
        modelAndView.addObject("age",18);
        //设置跳转的模板名称
        modelAndView.setViewName("/demo/view");// /demo/view.html
        return modelAndView;
    }

    //内置含有Model对象
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){

        model.addAttribute("name","李狗狗");
        model.addAttribute("age",18);

        return "/demo/view";
    }

    /**
     * 响应JSON数据（异步请求）
     *      使用  ResponseBody    将返回的数据以json格式返回
     *
     *      Java对象 -> JSON字符串 -> JS对象
     */
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","ligougou");
        emp.put("age",23);
        emp.put("salary",8000.0);

        return emp;
    }


    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","ligougou");
        emp.put("age",23);
        emp.put("salary",8000.0);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","guobaba");
        emp.put("age",25);
        emp.put("salary",12000.0);
        list.add(emp);

        return list;
    }

    //Cookie示例

    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie有效范围
        cookie.setPath("/community/alpha");
        // 设置cookie有效时间
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){

        System.out.println(code);

        return "get cookie";
    }

    // session示例

    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");

        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){

        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));

        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, String age){
        System.out.println(name);
        System.out.println(age);

        return CommunityUtil.getJSONString(0,"操作成功");
    }


}

















