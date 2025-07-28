package com.baseus.mcpserver;


import lombok.Builder;
import lombok.Data;

/**
 * @author jd
 * @date 2025/4/15 18:32
 */
@Builder
@Data
public class Book {

    private String title;

    private String author;

    private String description;
}
