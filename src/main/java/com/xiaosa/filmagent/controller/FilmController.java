package com.xiaosa.filmagent.controller;

import com.xiaosa.filmagent.service.SimpleChat;
import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class FilmController {
    @Resource
    private SimpleChat simpleChat;
    @GetMapping("/simple")
    public ApiResponse<String> simpleChat(@RequestParam String question, @RequestParam String conversationId){

        String result = simpleChat.chat(question,conversationId);
        System.out.println( result);
        return ApiResponse.success(result);
    }
}
