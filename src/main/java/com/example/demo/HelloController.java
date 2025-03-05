package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {
  @GetMapping("/")
  public ModelAndView index(ModelAndView mav) {
    mav.addObject("msg", "名前を入れてね");
    mav.setViewName("index");
    return mav;
  }

  @PostMapping("/")
  public ModelAndView send(ModelAndView mav, @RequestParam("name")String name) {
    mav.addObject("msg", "ようこそ、" + name + "さん");
    // フォームの DOM 上にも入力した文字列を残す
    mav.addObject("value", name);
    mav.setViewName("index");
    return mav;
  }
}
