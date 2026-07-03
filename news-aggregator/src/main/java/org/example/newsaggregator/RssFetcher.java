package org.example.newsaggregator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RssFetcher {
    public static void main(String[] args) {
        List<NewsItem> news = fetchAndParse();
    }

    public static List<NewsItem> fetchAndParse(){
            try{
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://www.tagesschau.de/infoservices/alle-meldungen-100~rss2.xml"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseString = response.body();

                InputStream inputStream =new ByteArrayInputStream(responseString.getBytes());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(inputStream);

                NodeList items = document.getElementsByTagName("item");

                List<NewsItem> newsList =new ArrayList<>();

                for (int i = 0; i < items.getLength(); i++) {
                    Element item = (Element) items.item(i);

                    String title = item.getElementsByTagName("title").item(0).getTextContent();
                    String description = item.getElementsByTagName("description").item(0).getTextContent();
                    String pubDate = item.getElementsByTagName("pubDate").item(0).getTextContent();
                    String link = item.getElementsByTagName("link").item(0).getTextContent();

                    if (link.contains("/tagesschau_in_100_sekunden/")
                            || link.contains("/video/")
                            || link.contains("/multimedia/")
                            || link.contains("/tagesschau_mit_gebaerdensprache/")
                            || link.contains("/tagesthemen/")
                            || link.contains("/tagesschau/")
                            || link.contains("/tagesschau_in_einfacher_sprache/")
                            || link.contains("/tagesschau_20_uhr/")
                            || link.contains("/wetter/"))
                    {
                        continue;
                    }

                    NewsItem newsItem = new NewsItem(title,description,link,pubDate);

                    newsList.add(newsItem);
                }
                return newsList;

            }catch (IOException e){
                System.out.println("Error_IO: " + e.getMessage());
                return null;
            }catch (InterruptedException e){
                System.out.println("Error_Inte:" + e.getMessage());
                return null;
            }catch (ParserConfigurationException e){
                System.out.println("Error_Parser" + e.getMessage());
                return null;
            }catch (SAXException e){
                System.out.println("Error_SAX: " + e.getMessage());
                return null;
            }catch (NullPointerException e){
                System.out.println("Error_Null: " + e.getMessage());
                return null;
            }
    }
}
