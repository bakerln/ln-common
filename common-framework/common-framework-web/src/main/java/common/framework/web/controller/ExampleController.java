package common.framework.web.controller;

import common.framework.web.jopi.Hello;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linan
 * @date 2020-07-27 14:54
 */
@RestController
@RequestMapping("/")
public class ExampleController {

    @GetMapping("/example")
    public Object example(){
        Hello a = new Hello();
        a.setName("linan");
        return a;
    }

}
