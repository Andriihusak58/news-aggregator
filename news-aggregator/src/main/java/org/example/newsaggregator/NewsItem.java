package org.example.newsaggregator;

public class NewsItem {
    private String title;
    private String link;
    private String pubDate;
    private String description;
    private String summary;

    public NewsItem(String title, String description, String link, String pubDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
    }

    public void setSummary(String summary) {

        this.summary = summary;

    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return link;
    }

    public String getDescription(){
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "NewsItem{title='" + title + "' description= '" + description + "', link='" + link + "', pubDate='" + pubDate + "'}";
    }
}
