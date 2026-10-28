package com.chsbk.mesh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String loginPage() {
        return "auth_page";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
