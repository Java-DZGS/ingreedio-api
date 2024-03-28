package pl.edu.pw.mini.ingreedio.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @RequestMapping("/")
    public String greeting() {
        return "Hello, world!";
    }
}
