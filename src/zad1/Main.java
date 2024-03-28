/**
 * @author Kalbarczyk Oskar S27773
 */

package zad1;


public class Main {
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        // ...
        // część uruchamiająca GUI

        App.setService(s);
        javafx.application.Application.launch(App.class);


    }
}
