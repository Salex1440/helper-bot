package ru.study.odin.odinbot.api;

import com.google.gson.Gson;
import okhttp3.*;
import ru.study.odin.odinbot.api.dto.Chat;
import ru.study.odin.odinbot.api.dto.User;
import ru.study.odin.odinbot.utils.PropertyReader;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TelegramBotApiImpl implements TelegramBotApi {

    private final OkHttpClient client;

    private static final String url = "https://api.telegram.org/bot";

    private final String token;

    private long nextUpdateId = 0;

    private final int longPollingTelegramTimeout;

    //region singleton implementation
    private static TelegramBotApiImpl instance = null;

    private TelegramBotApiImpl() {
        token = PropertyReader.getBotToken();
        longPollingTelegramTimeout = PropertyReader.getLongPollingTimeoutTelegram();
        int clientTelegramReadTimeoutInSeconds = PropertyReader.getClientTelegramReadTimeoutInSeconds();
        client = new OkHttpClient.Builder()
                .readTimeout(clientTelegramReadTimeoutInSeconds, TimeUnit.SECONDS)
                .build();
    }

    public static TelegramBotApiImpl getInstance() {
        if (instance == null) {
            instance = new TelegramBotApiImpl();
        }
        return instance;
    }

    @Override
    public User getMe() {
        Response response = sendRequest("getMe", null);
        if (!response.isSuccessful()) {
            System.out.println("Bot not found!!! Check bot token is correct in app.properties!");
            throw new RuntimeException("No bot found!");
        }
        return null;
    }

    @Override
    public Chat getChat(String chatId) {
        return null;
    }

    private Response sendRequest(String method, Object params) {
        String fullUrl = url + token + "/" + method;
        Request request;
        if (params == null) {
            request = new Request.Builder()
                    .url(fullUrl)
                    .build();
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(params);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            request = new Request.Builder()
                    .url(fullUrl)
                    .post(requestBody)
                    .build();
        }

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
