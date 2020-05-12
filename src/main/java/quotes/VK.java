package quotes;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.responses.GetResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VK {

    final static int ID = Integer.parseInt(System.getenv("VK_ID"));
    final static String TOKEN = System.getenv("VK_TOKEN");
    final static TransportClient transportClient = HttpTransportClient.getInstance();
    final static VkApiClient vk = new VkApiClient(transportClient);
    final static UserActor actor = new UserActor(ID, TOKEN);

    public static String getRandomQuote() {
        //42701798
        //46718830
        List<String> list = getQuotes(42701798, 400);
        return list.get(new Random().nextInt(list.size()));
    }

    public static List<String> getQuotes(int publicId, int count) {
        List<String> result = new ArrayList<>();
        int offset = 1; // 1 to skip first post which is often a pinned ad
        while (true) {
            try {
                GetResponse getResponse = vk.wall().get(actor)
                        .ownerId(-1 * publicId)
                        .count(count)
                        .offset(offset)
                        .execute();

                int size = getResponse.getItems().size();

                if (size > 0 && result.size() < count) {
                    getResponse.getItems().forEach(e -> {
                        String quote = e.getText();
                        if (quote.length() > 15) {
                            result.add(quote.replace("\n\n", "\n"));
                        }
                    });
                    offset += size;
                } else {
                    break;
                }
            } catch (ApiException | ClientException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}