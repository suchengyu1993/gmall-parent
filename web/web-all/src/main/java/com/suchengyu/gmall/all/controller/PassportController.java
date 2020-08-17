package com.suchengyu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * PsssportController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-03
 * @Description:
 */
@Controller
public class PassportController {


    @GetMapping("login.html")
    public String login(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl", originUrl);
        return "login";
    }
}
