package weather;

import java.util.Collections;

public class WeatherBean {
    private String cityName;
    private String countryName;
    private int temp;
    private int feelsLikeTemp;
    private String main;
    private String description;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public void setFeelsLikeTemp(int feelsLikeTemp) {
        this.feelsLikeTemp = feelsLikeTemp;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        main = main.toLowerCase();
        String emoji = "\uD83C\uDFB1";
        switch (main) {
            case "thunderstorm":
                emoji = "'\u0001\uF4A8'";
                break;
            case "drizzle":
                emoji = "\u0001\uF4A7";
                break;
            case "rain":
                emoji = "\u0000\u2614";
                break;
            case "snow":
                emoji = "\u2744";
                break;
            case "clear":
                emoji = "\u0000\u2600";
                break;
            case "clouds":
                emoji = "\u2601";
                break;

        }
        this.main = String.join("", Collections.nCopies(3, emoji)) + "\n" + main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getCityName() + " / " + getCountryName() + "\n" +
                getMain() + " (" + getDescription() + ")\n" +
                getTemp() + " \u2103 / " + "feels like " + getFeelsLikeTemp() + " \u2103 " + "\n"
                ;
    }
}