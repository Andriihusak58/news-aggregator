package org.example.newsaggregator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class NewsDatabase {

    private static final String DB_URL = System.getenv("DATABASE_URL");
    private static final String DB_USER = System.getenv("DATABASE_USER");
    private static final String DB_PASSWORD = System.getenv("DATABASE_PASSWORD");

    public static void main(String[] args) {

        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connect!");


            String sql = "CREATE TABLE IF NOT EXISTS news ("
                    + "id SERIAL PRIMARY KEY, "
                    + "title TEXT, "
                    + "description TEXT,"
                    + "link TEXT UNIQUE, "
                    + "pub_date TEXT, "
                    + "published_at TIMESTAMP, "
                    + "summary TEXT"
                    + ")";

            Statement statement = connection.createStatement();
            statement.execute(sql);
            System.out.println("Tabelle wurde erstellt!");

            List<NewsItem> newsList = RssFetcher.fetchAndParse();

            for (NewsItem news : newsList){

                insertNews(connection, news);
            }
            System.out.println("Новини записані в базу!");

            Statement countStmt = connection.createStatement();
            ResultSet rs = countStmt.executeQuery("SELECT COUNT(*) FROM news");
            System.out.println("Всього записів у базі: " + rs.getInt(1));

            generateSummaries(connection);

            connection.close();

        }catch (SQLException e){
            System.out.println("Error SQL: " + e.getMessage());
        }
    }



    public static void insertNews(Connection connection, NewsItem news) throws SQLException {
        String sql = "INSERT INTO news (title, description, link, pub_date, published_at) VALUES (?, ?, ?, ?, ?) ON CONFLICT (link) DO NOTHING";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, news.getTitle());
        pstmt.setString(2, news.getDescription());
        pstmt.setString(3, news.getLink());
        pstmt.setString(4, news.getPubDate());

        // Парсимо текстову дату в справжню
        try {
            OffsetDateTime date = OffsetDateTime.parse(news.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME);
            pstmt.setTimestamp(5, Timestamp.from(date.toInstant()));
        } catch (Exception e) {
            System.out.println("Не вдалося розпарсити дату: " + news.getPubDate());
            pstmt.setTimestamp(5, null);
        }

        pstmt.executeUpdate();
    }

    public static List<NewsItem> getAllNews(Connection connection) throws SQLException {

        List<NewsItem> newsList = new ArrayList<>();

        String sql = "SELECT title, description, link,pub_date FROM news";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()){

            String title = rs.getString("title");
            String description = rs.getString("description");
            String link = rs.getString("link");
            String pubData = rs.getString("pub_date");

            NewsItem news = new NewsItem(title, description, link, pubData);
            newsList.add(news);

        }
        return newsList;
    }
    public static void generateSummaries(Connection connection) throws SQLException{

        String selectSql = "SELECT id, link FROM news WHERE summary IS NULL";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectSql);

        while (rs.next()){

            int id = rs.getInt("id");

            String url = rs.getString("link");
            String text = ArticleFetcher.fetchArticleText(url);

            if (text == null || text.isEmpty()){

                System.out.println("Стаття " + id + " порожня, пропускаю");
                continue;

            }

            System.out.println("Довжина тексту: " + text.length());

            String summary = AiSummarizer.summarize(text);

            String updateSql = "UPDATE news SET summary = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateSql);
            pstmt.setString(1, summary);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

            System.out.println("Саммарі для новини " + id + ": " + summary);

        }

    }
}