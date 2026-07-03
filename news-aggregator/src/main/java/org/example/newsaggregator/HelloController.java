package org.example.newsaggregator;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    private final NewsService newsService;

    public HelloController(NewsService newsService){

        this.newsService = newsService;

    }

    @GetMapping("/hello")
    public String hello() {
        return "Привіт! Мій сервер працює.";
    }

    @GetMapping("/test")
    public String test(){

        return newsService.test();

    }

    @GetMapping("/update")
    public String update(){

        newsService.updateNews();
        return "Оновлення запущено! Перевір консоль.";
    }

    @GetMapping("/api/news")
    public List<NewsItem> getNews(){

        return newsService.getNews();

    }

}
