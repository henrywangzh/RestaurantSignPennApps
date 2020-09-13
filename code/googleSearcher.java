package com.example.sizzle;
import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class google {
    public static void main(String[] args) throws Exception{
        getUrlAndCovidProcedures("One Hundred Percent Cafe");
    }


public static void getUrlAndCovidProcedures(String text) throws Exception {

final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36";
        String query = text + "near me yelp";
final Document page = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8")).userAgent(USER_AGENT).get();
        //Traverse the results
        boolean yelpFinished = false;
        for (Element result : page.select("div > div > div > div > a")){


final String url = result.attr("href");
        if(!url.contains("google") && !url.contains("search") && !url.contains("#")) {
        // System.out.println(url);
        }
        if(url.contains("yelp.ca") && yelpFinished == false) {
        yelpFinished = true;
        System.out.println(url);
final Document yelpPage = Jsoup.connect(url).userAgent(USER_AGENT).get();

        Element result2 = yelpPage.select("section").first();

        for (Element result3 : result2.select("div > div > div")) {
        //System.out.println(result3);
        String fullProcedures = "";
        for (Element procedures : result3.select("div > div > span")) {

        fullProcedures += procedures.text() + " ";

        }

        fullProcedures = fullProcedures.replaceAll("\n[ \t]*\n", "\n");
        System.out.print(fullProcedures);

        }

        }


        }

        }
}