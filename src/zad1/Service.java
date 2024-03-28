/**
 * @author Kalbarczyk Oskar S27773
 */

package zad1;


import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;


public class Service implements IService {
    private String userSelectedCountry;
    private Locale userSelectedCountryLocale;
    private static final String OPEN_WEATHER_MAP_API_KEY = "8efd831c75c8dfcbcaa959dcd83858b9";
    private static final String OPEN_WEATHER_MAP_API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    public Service(String defaultCountry) {
        this.userSelectedCountry = defaultCountry.replaceAll("\\s", "");
        this.userSelectedCountryLocale = getLocaleFromNameOfCountry(userSelectedCountry);
    }

    private Locale getLocaleFromNameOfCountry(String country) {
        for (Locale locale : Locale.getAvailableLocales())
            if (country.equals(locale.getDisplayCountry()))
                return locale;
        return null;
    }

    @Override
    public Double getRateFor(String currency) throws IllegalArgumentException {
        if (currency.equals("") || currency.equals(" ")) {
            return null;
        }


        currency = currency.toUpperCase();

        if (userSelectedCountry.equals("USA")) {
            userSelectedCountryLocale = new Locale("en", "US");
        } else {
            userSelectedCountryLocale = getLocaleFromNameOfCountry(userSelectedCountry);
        }


        assert userSelectedCountryLocale != null;
        Currency userCurrency = Currency.getInstance(userSelectedCountryLocale);
        String currencyInUserSelectedCountry = userCurrency.getCurrencyCode();

        if (currency.equals(currencyInUserSelectedCountry)) {
            System.out.println("Comparing " + currency + " to " + currencyInUserSelectedCountry);
            return 1.0;
        }

        String FRANKFURTER_API_URL = "https://api.frankfurter.app/latest?from=%s";
        String urlString = String.format(FRANKFURTER_API_URL, currency);
        String response;
        try {
            response = getJsonStringResponse(urlString);
        } catch (IOException e) {
            System.err.println("Conversion From " + currencyInUserSelectedCountry.toUpperCase()
                    + " To " + currency.toUpperCase() + " Not Found!");
            return  null;
        }

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject ratesObject = jsonObject.getAsJsonObject("rates");

        Double rate = ratesObject.get(currencyInUserSelectedCountry.toUpperCase()).getAsDouble();
        System.out.println("Rate for " + currency.toUpperCase() + " to "
                + currencyInUserSelectedCountry.toUpperCase() + ": " + rate);
        return rate;

    }

    @Override
    public Double getNBPRate() {


        if (userSelectedCountry.equals("USA")) {
            userSelectedCountryLocale = new Locale("en", "US");
        } else {
            userSelectedCountryLocale = getLocaleFromNameOfCountry(userSelectedCountry);
        }

        assert userSelectedCountryLocale != null;
        Currency userDefinedCurrency = Currency.getInstance(userSelectedCountryLocale);
        String userDefinedCurrencyCode = userDefinedCurrency.getCurrencyCode().toLowerCase();

        if (userDefinedCurrencyCode.equals("pln")) {
            System.err.println("Converting PLN TO PLN");
            return 1.0;

        }


        Currency userCurrency = Currency.getInstance(userSelectedCountryLocale);
        String currencyInUserSelectedCountry = userCurrency.getCurrencyCode();

        String NBP_API_URL = "https://api.nbp.pl/api/exchangerates/rates/%s/%s/?format=json";
        String urlString = String.format(NBP_API_URL, "a", userDefinedCurrencyCode);

        System.out.println(urlString);
        String response;
        try {
            response = getJsonStringResponse(urlString);
        } catch (IOException e) {
            urlString = String.format(NBP_API_URL, "b", userDefinedCurrencyCode);
            try {
                response = getJsonStringResponse(urlString);
            } catch (IOException f) {
                urlString = String.format(NBP_API_URL, "c", userDefinedCurrencyCode);
                try {
                    response = getJsonStringResponse(urlString);
                } catch (IOException ignore) {
                    return null;
                }
            }
        }


        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonArray ratesArray = jsonObject.getAsJsonArray("rates");


        JsonObject rateObject = ratesArray.get(0).getAsJsonObject();


        Double rate = rateObject.get("mid").getAsDouble();
        System.out.println("Rate for " + userDefinedCurrencyCode.toUpperCase() + " to "
                + currencyInUserSelectedCountry.toUpperCase() + ": " + rate);
        return rate;

    }

    @Override
    public String getWeather(String city) {
        if (userSelectedCountry == null || userSelectedCountry.equals("")) {
            return null;
        }

        Locale locale;
        String countryCode = "";
        String urlString;

        city = city.replaceAll("\\s", "");

        if (userSelectedCountry.equals("USA")) {
            if(city.equals("NewYork")) {
                urlString = String.format(OPEN_WEATHER_MAP_API_URL, "New%20York" + "," + "US", OPEN_WEATHER_MAP_API_KEY);
            }else {
                 urlString = String.format(OPEN_WEATHER_MAP_API_URL, city + "," + "US", OPEN_WEATHER_MAP_API_KEY);
            }
        } else {
            locale = new Locale("", userSelectedCountry);
            countryCode = locale.getCountry();
            countryCode = countryCode.replaceAll("\\s", "");

            urlString = String.format(OPEN_WEATHER_MAP_API_URL, city + "," + countryCode, OPEN_WEATHER_MAP_API_KEY);
        }


        String response;
        try {
            response = getJsonStringResponse(urlString);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Weather for " + city + " in " + countryCode + " not found");
            return null;
        }

        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray weatherArray = jsonObject.getAsJsonArray("weather");

        String temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble() + " °C ";
        String weatherDescription = weatherArray.get(0).getAsJsonObject().get("description").getAsString();
        String weather =  temperature + " " + weatherDescription;

        System.out.println(weather);

        return weather;
    }

    public void selectCountry(String userSelectedCountry) {
        this.userSelectedCountry = userSelectedCountry;
        this.userSelectedCountryLocale = getLocaleFromNameOfCountry(userSelectedCountry);
    }


    private String getJsonStringResponse(String urlString) throws IOException {
        String response = null;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            // System.out.println(line);
            stringBuilder.append(line);
        }
        response = stringBuilder.toString();
        reader.close();
        connection.disconnect();

        System.out.println(response);
        return response;
    }

}
