package com.baseus.mcpserver;


import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author jd
 * @date 2025/4/15 18:18
 */
@Slf4j
@Service
public class FileStreamService {


    @Tool(name = "create_file", description = "根据信息在本地创建文件")
    public String createFile(@ToolParam(description = "文件名") String fileName, @ToolParam(description = "文件路径") String path
            , @ToolParam(description = "文件内容") String count) {
        //  创建文件逻辑  创建文件
        try {
            File file = new File(path + fileName);
            if (file.createNewFile()) {
                log.info("File created: " + file.getName());
                //  创建文件成功  写入文件
                FileWriter writer = new FileWriter(file);
                writer.write(count);
                writer.close();
                return "success";
            } else {
                log.info("File already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "fail";
    }
    @Tool(name = "delete_file", description = "根据文件信息删除文件")
    public String deleteFile(@ToolParam(description = "文件名") String fileName, @ToolParam(description = "文件路径") String path) {
        //  删除文件逻辑  删除文件
        File file = new File(path + fileName);
        if (file.delete()) {

            log.info("File deleted: " + file.getName());
        }
        return "success";
    }
    @Tool(name = "open_chrome", description = "打开浏览器并跳转到对应的网址")
    public String operate(@ToolParam(description = "网址") String url) {
        // 设置 chromedriver 路径
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");

        // 初始化 ChromeDriver
        WebDriver driver = new ChromeDriver();

        // 打开网页
        driver.get(url);

        // 打印网页标题
        System.out.println("Title: " + driver.getTitle());

        // 关闭浏览器
//        driver.quit();
        return "success";
    }


}
