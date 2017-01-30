package org.aksw.kgapi.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/bla")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}