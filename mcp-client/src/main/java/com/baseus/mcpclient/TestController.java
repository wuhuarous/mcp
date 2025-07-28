package com.baseus.mcpclient;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jd
 * @date 2025/5/26 16:08
 */
@RestController
public class TestController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String test(@RequestParam("prompt") String prompt) {
        return chatClient.prompt().user(prompt).call().content();
    }
}
