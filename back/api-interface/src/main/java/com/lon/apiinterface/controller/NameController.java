package com.lon.apiinterface.controller;

import com.lon.clientsdk.model.User;
import com.lon.clientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 查询名称
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/")
    public String getNameByGet(String name) {
        return "你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
//        if(!accessKey.equals("yuan")){
//            throw new RuntimeException("无权限");
//        }
        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限");
        }
//        String serverSign = SignUtils.genSign(body,"shen");
//        if(!sign.equals(serverSign)){
//            throw new RuntimeException("无权限");
//        }
        return "用户名字是" + user.getName();
    }
}
