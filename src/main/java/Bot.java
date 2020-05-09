import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {

        BasicConfigurator.configure();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        setButtons(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(Message message, File file, String caption) {
        SendPhoto photoMsg;
        try {
            photoMsg = new SendPhoto();
            photoMsg.setPhoto(file);
            photoMsg.setChatId(message.getChatId());
            photoMsg.setCaption(caption);
            this.execute(photoMsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public File getRandomPhoto() {
        File file;
        try {
            file = File.createTempFile("temp", "jpg");
            List<String> list = Cloudinary.getAllPhotos();
            String str = list.get(new Random().nextInt(list.size()));
            FileUtils.copyURLToFile(new URL(str), file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return new File("temp.jpg");
        }
    }

    public static String getRandomQuote() {
        StringBuilder sb = new StringBuilder();
        URL url;
        URLConnection urlConn = null;
        try {
            url = new URL("https://api.forismatic.com/api/1.0/?method=getQuote&key=457653&format=text&lang=ru");
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

    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();
            switch (text) {
                case "/help":
                    sendMsg(message,
                            "'/w city-name' - weather report" + "\n" +
                                 "'/rq' - random quote with photo");
                    break;
                case "/about":
                    sendMsg(message, "AlpVolkiBot by Vitaly Kolesnikov (c) 2020");
                    break;
                case "/rq": {
                    sendPhoto(message, getRandomPhoto(), getRandomQuote());
                }
                default:
                    if (text.startsWith("/w ")) {
                        sendMsg(message, Weather.getWeather(message.getText().replaceAll("/w ", ""), model));
                    }
            }
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/about"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }
}