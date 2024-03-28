package zad1;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebBrowser extends Region {
    private final WebView browser = new WebView();
    private final WebEngine webEngine = browser.getEngine();

    public WebBrowser() {
        webEngine.load("https://en.wikipedia.org/wiki/Main_Page");
        getChildren().add(browser);
    }

    public void loadPage(String city, String country) {
        String formattedCity = city.replace(" ", "_");


        String url = "https://en.wikipedia.org/wiki/" + formattedCity + ",_" + country;
        System.out.println("Loading Page: " + url);

        webEngine.load(url);
    }


    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        layoutInArea(browser, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
    }
}