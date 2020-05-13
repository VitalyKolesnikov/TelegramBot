package weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class WeatherHandler {

    public static String getWeather(String message, WeatherBean weatherBean) {
        StringBuilder str = new StringBuilder();
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=" + System.getenv("WEATHERMAP_TOKEN"));
            Scanner in = new Scanner((InputStream) url.getContent());
            while (in.hasNext()) {
                str.append(in.nextLine());
            }
        } catch (IOException e) {
            return "Город не найден";
        }

        JSONObject object = new JSONObject(str.toString());
        weatherBean.setCityName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        weatherBean.setTemp((int) Math.round(main.getDouble("temp")));
        weatherBean.setFeelsLikeTemp((int) Math.round(main.getDouble("feels_like")));

        JSONObject sys = object.getJSONObject("sys");
        weatherBean.setCountryName(sys.getString("country"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            weatherBean.setMain((String) obj.get("main"));
            weatherBean.setDescription((String) obj.get("description"));
        }

        return weatherBean.toString();
    }
}
