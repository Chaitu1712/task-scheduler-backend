package com.scheduler.task_scheduler_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactAppController {

    @RequestMapping(value = {"/{path:[^\\.]*}", "/**/{path:^(?!api$).*$}"})
    public String redirect() {
        return "forward:/";
    }
}