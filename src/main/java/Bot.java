import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
import photo.Cloudinary;
import quotes.QuoteDAO;
import quotes.QuoteHandler;
import weather.WeatherBean;
import weather.WeatherHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        //sendMessage.enableMarkdown(false);
        sendMessage.enableHtml(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        //setButtons(sendMessage);
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

    public void onUpdateReceived(Update update) {
        WeatherBean weatherBean = new WeatherBean();
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();
            if (text.startsWith("/help")) {
                sendMsg(message,
                        "/w - погода (например /w Moscow, /w Пермь)" + "\n" +
                                "/rq - пацанская цитатка" + "\n" +
                                "/tor - принципы Торетто"
                );
            } else if (text.startsWith("/about")) {
                sendMsg(message, "AlpVolkiBot by Vitaly Kolesnikov (c) 2020");
            } else if (text.startsWith("/rq")) {
                sendPhoto(message, Cloudinary.getRandomPhoto(), QuoteHandler.getRandomQuote());
            } else if (text.startsWith("/tor")) {
                sendMsg(message, QuoteDAO.getTorettoRules());
            } else if (text.startsWith("/w ")) {
                sendMsg(message, WeatherHandler.getWeather(message.getText().replaceAll("/w ", ""), weatherBean));
            } else if (text.startsWith("/ssm ")) {
                sendBotMessage(text.replaceAll("/ssm ", ""));
            } else if (text.startsWith("/w")) {
                sendMsg(message, WeatherHandler.getWeather("Moscow", weatherBean) +
                        WeatherHandler.getWeather("Rostov-on-Don", weatherBean) +
                        WeatherHandler.getWeather("Perm", weatherBean) +
                        WeatherHandler.getWeather("Tyumen", weatherBean));
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

    public void sendBotMessage(String text) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.telegram.org/bot" + System.getenv("BOT_TOKEN") + "/sendMessage");

        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("chat_id", System.getenv("SSM_CHAT_ID")));
        params.add(new BasicNameValuePair("text", text));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }
}