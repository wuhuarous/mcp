package com.baseus.mcpclient;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jd
 * @date 2025/5/26 16:02
 */
@Configuration
public class Config {


    @Bean
    public ChatClient  chatClient(ChatClient.Builder chatClient, ChatMemory chatMemory, ToolCallbackProvider provider) {

        return chatClient
                // PromptChatMemoryAdvisor.builder(chatMemory).build() 保留聊天记忆
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(provider)
                .build();
    }

}
