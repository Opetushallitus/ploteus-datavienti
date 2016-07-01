package fi.vm.sade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String getString() {
        return "index.html";
    }

    @RequestMapping("/buildversion.txt")
    @ResponseBody
    public String getBuildversion() {
        return "Alive";
    }

}
