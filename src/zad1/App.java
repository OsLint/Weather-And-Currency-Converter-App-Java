package zad1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class App extends Application {

    private static IService service;
    private TextField countryTextField;
    private TextField cityTextField;
    private TextField currencyTextField;
    private BorderPane borderPane;
    private ComboBox<String> countryComboBox;
    private ComboBox<String> cityComboBox;
    private ComboBox<String> currencyComboBox;

    private final Label weatherLabel = new Label(" ");
    private final Label currencyRateLabel = new Label(" ");
    private final Label nbpRateLabel = new Label(" ");
    WebBrowser webBrowser;
    private Stage stage;


    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        initializeStage();
        initWindow();
        Scene scene = new Scene(borderPane, 1280, 720);
        scene.getStylesheets().add("zad1/css/styles.css");

        stage.setScene(scene);
        stage.show();
    }

    private void initializeStage() {
        stage.setTitle("Weather & Currency Converter");
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.getIcons().add(new Image("file:appicon.png"));
    }

    public void initWindow() {
        BorderPane toolBar = new BorderPane();
        HBox hBoxLeft = new HBox();
        HBox hBoxRight = new HBox();

        hBoxRight.setSpacing(10);

        webBrowser = new WebBrowser();
        borderPane = new BorderPane();
        countryTextField = createTextField("Type Country...");
        cityTextField = createTextField("Type City...");
        currencyTextField = createTextField("Type Currency...");
        borderPane.setCenter(webBrowser);


        ObservableList<String> countryList = FXCollections.observableArrayList(
                "Poland", "USA", "Germany", "France", "Japan");
        countryComboBox = createComboBox(countryList, countryTextField);

        ObservableList<String> cityList = FXCollections.observableArrayList(
                "Warsaw", "New York", "Berlin", "Paris", "Tokyo");
        cityComboBox = createComboBox(cityList, cityTextField);

        ObservableList<String> currencyList = FXCollections.observableArrayList(
                "PLN", "USD", "EUR", "JPY", "GBP");
        currencyComboBox = createComboBox(currencyList, currencyTextField);

        Button submitButton = new Button("Search");
        submitButton.setOnAction(event -> handleSearch());

        hBoxLeft.getChildren().addAll(countryComboBox, cityComboBox, currencyComboBox, submitButton);
        hBoxRight.getChildren().addAll(weatherLabel, currencyRateLabel, nbpRateLabel);
        toolBar.setLeft(hBoxLeft);
        toolBar.setRight(hBoxRight);
        borderPane.setTop(toolBar);
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }


    private ComboBox<String> createComboBox(ObservableList<String> list, TextField textField) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(list);
        comboBox.setEditable(true);
        comboBox.setMaxWidth(150);
        comboBox.setPromptText(textField.getPromptText());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                textField.setVisible(true);
                textField.requestFocus();
            } else {
                textField.setVisible(false);
            }
        });
        return comboBox;
    }

    private void handleSearch() {
        String selectedCountry = getSelectedValueOrDefault(countryComboBox, countryTextField);
        String selectedCity = getSelectedValueOrDefault(cityComboBox, cityTextField);
        String selectedCurrency = getSelectedValueOrDefault(currencyComboBox, currencyTextField);

        System.out.println(selectedCity + " " + selectedCountry + " " + selectedCurrency);

        double currencyRate = 0.0;
        double nbpRate = 0.0;
        String weatherResult = "";

        webBrowser.loadPage(selectedCity, selectedCountry);
        service.selectCountry(selectedCountry);

        try {
            currencyRate = service.getRateFor(selectedCurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            nbpRate = service.getNBPRate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            weatherResult = service.getWeather(selectedCity);
        } catch (Exception e) {
            e.printStackTrace();
        }



    Text weatherText = new Text("Weather in ");
    Text selectedCityText = new Text(selectedCity);
    Text weatherResultText = new Text(": " + weatherResult);

    Text currencyRateText1 = new Text("Currency Rate for 1 ");
    Text selectedCurrencyText = new Text(selectedCurrency);
    Text currencyRateText2 = new Text(" in ");
    Text selectedCountryText = new Text(selectedCountry);
    Text currencyRateText3 = new Text(": " + currencyRate);

    Text nbpRateText = new Text("NBP Rate: " + nbpRate);


    weatherText.setFill(Color.BLACK);
    selectedCityText.setFill(Color.BLUE);
    weatherResultText.setFill(Color.GREEN);

    currencyRateText1.setFill(Color.BLACK);
    selectedCurrencyText.setFill(Color.BLUE);
    currencyRateText2.setFill(Color.BLACK);
    selectedCountryText.setFill(Color.BLUE);
    currencyRateText3.setFill(Color.GREEN);

    nbpRateText.setFill(Color.RED);


    weatherLabel.setGraphic(null);
    weatherLabel.setText("");
    weatherLabel.setGraphic(new HBox(weatherText, selectedCityText, weatherResultText));

    currencyRateLabel.setGraphic(null);
    currencyRateLabel.setText("");
    currencyRateLabel.setGraphic(new HBox(currencyRateText1, selectedCurrencyText, currencyRateText2, selectedCountryText, currencyRateText3));

    nbpRateLabel.setGraphic(null);
    nbpRateLabel.setText("");
    nbpRateLabel.setGraphic(nbpRateText);


    }


    private String getSelectedValueOrDefault(ComboBox<String> comboBox, TextField textField) {
        String selectedValue = comboBox.getValue();
        if (selectedValue == null || selectedValue.isEmpty()) {
            selectedValue = textField.getText();
        }
        return selectedValue;
    }


    public static void setService(IService service) {
        App.service = service;
    }
}





