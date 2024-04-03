package pl.edu.pw.mini.ingreedio.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class HelloWorldController {
    @GetMapping
    public String greeting() {
        return "Hello, world!";
    }
}
