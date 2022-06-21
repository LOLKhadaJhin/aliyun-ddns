package com.nhwb.aliyun.controller;

import com.nhwb.aliyun.ddns.DDNSRefresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DDNSController {
    @Autowired
    private DDNSRefresh DDNSRefresh;
    @Value("${visit.username}")
    private String name;
    @Value("${visit.password}")
    private String pass;

    @GetMapping({"/{username}/{password}/{refresh}"})
    @ResponseBody
    public String refresh(@PathVariable String username, @PathVariable String password, @PathVariable String refresh) {
        if (username.equals(name) && password.equals(pass)) {
            if (refresh.equals("refresh") || refresh.equals("sx")) {
                DDNSRefresh.setOldIPIsNull();
            }
            DDNSRefresh.refresh();
            return "域名IP：" + DDNSRefresh.getOldIP() + "。公网IP：" + DDNSRefresh.getNewIP() + "。";
        } else {
            return "visit.username或visit.password错误！";
        }
    }
}
