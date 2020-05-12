package photo;

import com.cloudinary.Api;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Cloudinary {

    static Map config = ObjectUtils.asMap(
            "cloud_name", System.getenv("CLOUDINARY_NAME"),
            "api_key", System.getenv("CLOUDINARY_KEY"),
            "api_secret", System.getenv("CLOUDINARY_SECRET"));

    static com.cloudinary.Cloudinary cloudinary = new com.cloudinary.Cloudinary(config);
    static Api api = cloudinary.api();

    static public File getRandomPhoto() {
        File file;
        String key = new Random().nextInt(6) + String.valueOf(new Random().nextInt(10)); // 00 - 59
        try {
            file = File.createTempFile("temp", "jpg");
            List<String> list = searchPhotos(key);
            String str = list.get(new Random().nextInt(list.size()));
            FileUtils.copyURLToFile(new URL(str), file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return new File("temp.jpg");
        }
    }

    static public List<String> searchPhotos(String key) {
        ArrayList<String> listRes = new ArrayList<>();
        try {
            ApiResponse resp = cloudinary.search()
                    .expression("filename: '*" + key + "*'")
                    .execute();
            JSONArray arr2 = new JSONArray(new JSONArray(resp.values()).get(0).toString());
            arr2.forEach(e -> listRes.add(((JSONObject) e).get("secure_url").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listRes;
    }

    public static List<String> getAllPhotos() throws Exception {

        JSONObject outerObject;
        String jsonNext = null;
        boolean ifWeHaveMoreResources = true;
        ArrayList<String> listRes = new ArrayList<>();

        while (ifWeHaveMoreResources) {
            outerObject = new JSONObject(api.resources(ObjectUtils.asMap("max_results", 500, "next_cursor", jsonNext)));

            if (outerObject.has("next_cursor")) {
                jsonNext = outerObject.get("next_cursor").toString();
                ifWeHaveMoreResources = true;
            } else {
                ifWeHaveMoreResources = false;
            }

            JSONArray jsonArray = outerObject.getJSONArray("resources");

            for (int i = 0, size = jsonArray.length(); i < size; i++) {
                JSONObject objectInArray = jsonArray.getJSONObject(i);
                String url = objectInArray.get("secure_url").toString();
                listRes.add(url);
            }
        }
        return listRes;
    }
}