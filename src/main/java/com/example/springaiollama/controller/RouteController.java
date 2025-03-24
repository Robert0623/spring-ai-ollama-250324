package com.example.springaiollama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    @GetMapping("/hotel")
    public String hotel() {
        return "hotel";
    }
}
