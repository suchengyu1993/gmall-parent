package com.suchengyu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TestController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@Controller
public class TestController {

    @GetMapping("/test")
    public String test(ModelMap modelMap){

        return "/item/index";
    }


}
