package com.baseus.mcpserver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jd
 * @date 2025/4/15 18:18
 */
@Slf4j
@Service
public class QueryService {

    private static final Map<String, Book> book = new HashMap<>();

    static {


    }

    /**
     * 通过书名查询书籍信息
     *
     * @param title
     * @return
     */

    @Tool(name = "get_book_info_by_title", description = "Obtain detailed information through the book title")
    public Book findBooksByTitle(String title) {

        return  book.getOrDefault(title, null);
    }

    @Tool(name = "book", description = "get all books list")
    public List<Book> getBook() {
        return book.keySet().stream().map(book::get).toList();
    }

    @Tool(name = "saveBook", description = "根据信息创建书籍存储到数据库")
    public String saveBook(@ToolParam(description = "书籍标题") String title, @ToolParam(description = "作者") String author
            ,  @ToolParam(description = "描述") String description) {
        book.put(title, Book.builder().title(title).author(author).description(description).build());
        log.info("保存成功:{}",  title);
        return "fail";
    }

}
