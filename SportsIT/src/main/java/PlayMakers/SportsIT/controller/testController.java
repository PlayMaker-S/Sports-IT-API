package PlayMakers.SportsIT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class testController {
    @RequestMapping
    public String test(){
        return "payment";
    }


}
