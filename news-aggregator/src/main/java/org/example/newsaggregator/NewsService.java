package org.example.newsaggregator;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    private static final String DB_URL = "jdbc:sqlite:news.db";

    @Scheduled(fixedRate = 3600000)
    public void scheduledUpdate(){

        System.out.println("Автоматичне оновлення запущено...");
        updateNews();

    }

    public String test(){
        return "Сервіс працює!";
    }

    public void updateNews(){

        try{

            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connect!");

            String sql = "CREATE TABLE IF NOT EXISTS news ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT, "
                    + "description TEXT, "
                    + "link TEXT UNIQUE, "
                    + "pub_date TEXT, "
                    +"summary TEXT"
                    +")";

            Statement statement = connection.createStatement();
            statement.execute(sql);

            List<NewsItem> newsList = RssFetcher.fetchAndParse();
            for (NewsItem news : newsList){
                NewsDatabase.insertNews(connection, news);
            }
            System.out.println("Новини записані в базу!");

            NewsDatabase.generateSummaries(connection);

            connection.close();
            System.out.println("Оновлення завершено!");

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public List<NewsItem> getNews() {
        List<NewsItem> newsList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(DB_URL);

            String sql = "SELECT title, description, link, pub_date, summary FROM news WHERE summary IS NOT NULL ORDER BY id DESC LIMIT 140";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()){

                String title = rs.getString("title");
                String description = rs.getString("description");
                String link = rs.getString("link");
                String pubDate = rs.getString("pub_date");
                String summary = rs.getString("summary");

                NewsItem news = new NewsItem(title, description, link, pubDate);
                news.setSummary(summary);
                newsList.add(news);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
        return  newsList;
    }
}
