package com.lion.BMWtour.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlertController {
    @GetMapping("/common/alertMsg")
    public String alertMsg(HttpServletRequest request, Model model) {
        String msg = (String) request.getSession().getAttribute("msg");
        String url = (String) request.getSession().getAttribute("url");
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "common/alertMsg"; // alertMsg.html 템플릿 반환
    }
    }
