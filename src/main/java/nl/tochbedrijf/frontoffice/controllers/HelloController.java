package nl.tochbedrijf.frontoffice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello world!" + String.valueOf(Math.floor(6 * Math.random())+1);
    }
}
