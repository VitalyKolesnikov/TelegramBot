package quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Objects;

public class Forismatic {

    final static String LINK = "https://api.forismatic.com/api/1.0/?method=getQuote&key=457653&format=text&lang=ru";

    public static String getRandomQuote() {
        StringBuilder sb = new StringBuilder();
        URL url;
        URLConnection urlConn = null;
        try {
            url = new URL(LINK);
            urlConn = url.openConnection();
            urlConn.addRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStreamReader in = new InputStreamReader(Objects.requireNonNull(urlConn).getInputStream(),
                Charset.defaultCharset())) {

            urlConn.setReadTimeout(60 * 1000);
            if (urlConn.getInputStream() != null) {
                BufferedReader bufferedReader = new BufferedReader(in);
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Random quote";
        }
        return sb.toString();
    }

}