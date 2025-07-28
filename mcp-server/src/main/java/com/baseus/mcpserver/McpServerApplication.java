package com.baseus.mcpserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }



    @Bean
    public ToolCallbackProvider toolCallbackProvider(QueryService queryService) {
       return MethodToolCallbackProvider.builder().toolObjects(queryService).build();
    }

    @Bean
    public ToolCallbackProvider toolFileCallbackProvider(FileStreamService fileStreamService) {
        return MethodToolCallbackProvider.builder().toolObjects(fileStreamService).build();
    }
}
