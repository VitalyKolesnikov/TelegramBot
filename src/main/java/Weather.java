import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Weather {

    public static String getWeather(String message, Model model) {
        StringBuilder str = new StringBuilder();
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=95dd4f93ba161d7b679e8009d4699913");
            Scanner in = new Scanner((InputStream) url.getContent());
            while (in.hasNext()) {
                str.append(in.nextLine());
            }
        } catch (IOException e) {
            return "City not found";
        }

        JSONObject object = new JSONObject(str.toString());
        model.setCityName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        model.setTemp((int) Math.round(main.getDouble("temp")));
        model.setFeelsLikeTemp((int) Math.round(main.getDouble("feels_like")));

        JSONObject sys = object.getJSONObject("sys");
        model.setCountryName(sys.getString("country"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            model.setMain((String) obj.get("main"));
            model.setDescription((String) obj.get("description"));
        }

        return model.toString();
    }
}
