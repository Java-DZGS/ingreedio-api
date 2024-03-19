package pl.edu.pw.mini.ingreedio.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {
    @RequestMapping("/")
    public @ResponseBody String greeting() {
        return "Hello, world!";
    }
}
