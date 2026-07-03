package org.example.newsaggregator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class ArticleFetcher {

    public static void main(String[] args){

        String url = "https://www.tagesschau.de/wirtschaft/unternehmen/google-rekordstrafe-eu-100.html";
        String text = fetchArticleText(url);
        System.out.println("Довжина тексту: " + text.length());
        System.out.println("---");
        System.out.println(text);

    }

    public static String fetchArticleText (String url) {
        try{

            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p.textabsatz");

            StringBuilder articleText = new StringBuilder();
            for (Element p : paragraphs) {

                articleText.append(p.text()).append(" ");
            }
            return articleText.toString();
        }catch (IOException e){
            System.out.println("Error fetching article: " + e.getMessage());
            return null;
        }
    }
}