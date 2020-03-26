package com.how2java.tmall.web;

import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Page4Navigator<User> list(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {

        start = start < 0 ? 0 : start;
        Page4Navigator<User> page = userService.lsit(start, size, 5);
        return page;
    }




}
