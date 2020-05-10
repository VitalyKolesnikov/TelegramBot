import com.cloudinary.Api;
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
        try {
            file = File.createTempFile("temp", "jpg");
            List<String> list = getAllPhotos();
            String str = list.get(new Random().nextInt(list.size()));
            FileUtils.copyURLToFile(new URL(str), file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return new File("temp.jpg");
        }
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