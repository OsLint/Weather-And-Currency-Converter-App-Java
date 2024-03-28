package zad1;

public interface IService {
    Double getRateFor(String currency) throws IllegalArgumentException;

    Double getNBPRate();

    String getWeather(String city);

    void selectCountry(String userSelectedCountry);
}
